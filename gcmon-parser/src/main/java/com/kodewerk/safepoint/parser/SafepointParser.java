package com.kodewerk.safepoint.parser;

import com.kodewerk.safepoint.event.ApplicationRuntime;
import com.kodewerk.safepoint.event.JVMEvent;
import com.kodewerk.safepoint.event.JVMStart;
import com.kodewerk.safepoint.event.JVMTermination;
import com.kodewerk.safepoint.event.Safepoint;
import com.kodewerk.safepoint.event.SafepointCause;
import com.kodewerk.safepoint.io.SafepointLogEntry;
import com.kodewerk.safepoint.io.SafepointLogLine;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import static com.kodewerk.safepoint.parser.SafepointRules.*;

public class SafepointParser {

    private ConcurrentHashMap<String,Double> eventTime = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,SafepointCause> safepointCause = new ConcurrentHashMap<>();

    private JVMEventConsumer outbox;
    private double currentTime = 0.0d;

    final private HashMap<SafepointParseRule, BiConsumer<SafepointLogEntry, SafepointLogLine>> parseRules;

    {
        parseRules = new HashMap<>();
        parseRules.put(APPLICATION_TIME, this::applicationTime);
        parseRules.put(ENTERING_SAFEPOINT_REGION,this::enteringSafepointRegion);
        parseRules.put(LEAVING_SAFEPOINT_REGION,this::leavingSafepointRegion);
        parseRules.put(SAFEPOINT_DETAILS,this::safepointDetails);
        parseRules.put(JVM_START, this::jvmStart);
        parseRules.put(JVM_TERMINATION, this::jvmTermination);
    }

    public SafepointParser() {}

    public SafepointParser(JVMEventConsumer outBox) {
        this.outbox = outBox;
    }

    public void setEventConsumer(JVMEventConsumer outbox) {
        this.outbox = outbox;
    }

    public void parse(SafepointLogLine logLine) {
        Optional<AbstractMap.SimpleEntry<SafepointParseRule, SafepointLogEntry>> ruleToApply = parseRules.keySet().stream()
                .map(rule -> new AbstractMap.SimpleEntry<>(rule, rule.parse(logLine.getLine())))
                .filter(tuple -> tuple.getValue() != null)
                .filter(tuple -> tuple.getValue().matched())
                .findFirst();

        try {
            if (ruleToApply.isPresent() && ruleToApply.get().getValue().matched()) {
                parseRules.get(ruleToApply.get().getKey()).accept(ruleToApply.get().getValue(), logLine);
            } else {
                System.out.println("Missed: " + logLine.toString());
            }
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            t.printStackTrace();
        }
    }

    private void applicationTime(SafepointLogEntry logEntry, SafepointLogLine logLine) {
        record( new ApplicationRuntime(logLine.getId(),logEntry.getTimeOfEvent(),logEntry.getDuration()));
    }

    private void enteringSafepointRegion(SafepointLogEntry logEntry, SafepointLogLine logLine) {
        this.eventTime.put(logLine.getId(),logEntry.getTimeOfEvent());
        this.safepointCause.put(logLine.getId(), SafepointCause.valueOf(logEntry.getGroup(1)));
    }

    private void leavingSafepointRegion(SafepointLogEntry logEntry, SafepointLogLine logLine) {
        currentTime = logEntry.getTimeOfEvent();
    }

    private void safepointDetails(SafepointLogEntry logEntry, SafepointLogLine logLine) {
        String sessionID = logLine.getId();
        record(new Safepoint(sessionID,eventTime.get(sessionID),safepointCause.get(sessionID),logEntry.getDoubleGroup(3),logEntry.getDoubleGroup(1)));
    }

    private void jvmStart(SafepointLogEntry logEntry, SafepointLogLine logLine) {
        record(new JVMStart(logLine.getId()));
    }

    private void jvmTermination(SafepointLogEntry logEntry, SafepointLogLine logLine) {
        record(new JVMTermination(logLine.getId(),logEntry.getTimeOfEvent()));
    }

    private void record(JVMEvent event) {
        outbox.offer(event);
        if ( eventTime.contains(event.getSessionID())) {
            eventTime.remove(event.getSessionID());
            safepointCause.remove(event.getSessionID());
        }
    }
}
