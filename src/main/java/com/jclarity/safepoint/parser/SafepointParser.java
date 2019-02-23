package com.jclarity.safepoint.parser;

import com.jclarity.safepoint.event.ApplicationRuntime;
import com.jclarity.safepoint.event.EventSink;
import com.jclarity.safepoint.event.JVMEvent;
import com.jclarity.safepoint.event.JVMTermination;
import com.jclarity.safepoint.event.Safepoint;
import com.jclarity.safepoint.event.SafepointCause;
import com.jclarity.safepoint.io.SafepointLogEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.jclarity.safepoint.parser.SafepointRules.*;

public class SafepointParser implements EventSink<String> {

    private EventConsumer outbox;
    private double currentTime = 0.0d;
    private double eventTime = -1.0d;
    private SafepointCause safepointCause = null;

    final private HashMap<SafepointParseRule, BiConsumer<SafepointLogEntry, String>> parseRules;

    {
        parseRules = new HashMap<>();
        parseRules.put(APPLICATION_TIME, this::applicationTime);
        parseRules.put(ENTERING_SAFEPOINT_REGION,this::enteringSafepointRegion);
        parseRules.put(LEAVING_SAFEPOINT_REGION,this::leavingSafepointRegion);
        parseRules.put(SAFEPOINT_DETAILS,this::safepointDetails);
        parseRules.put(JVM_TERMINATION, this::jvmTermination);
    }

    public SafepointParser() {}

    public SafepointParser(EventConsumer outBox) {
        this.outbox = outBox;
    }

    public void setEventConsumer(EventConsumer outbox) {
        this.outbox = outbox;
    }

    public void parse(String line) {
        Optional<AbstractMap.SimpleEntry<SafepointParseRule, SafepointLogEntry>> ruleToApply = parseRules.keySet().stream()
                .map(rule -> new AbstractMap.SimpleEntry<>(rule, rule.parse(line)))
                .filter(tuple -> tuple.getValue() != null)
                .filter(tuple -> tuple.getValue().matched())
                .findFirst();

        try {
            if (ruleToApply.isPresent() && ruleToApply.get().getValue().matched()) {
                parseRules.get(ruleToApply.get().getKey()).accept(ruleToApply.get().getValue(), line);
            } else {
                System.out.println("Missed: " + line);
            }
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            t.printStackTrace();
        }
    }

    private void applicationTime(SafepointLogEntry logEntry, String logLine) {
        record( new ApplicationRuntime(logEntry.getTimeOfEvent(),logEntry.getDuration()));
    }

    private void enteringSafepointRegion(SafepointLogEntry logEntry, String logLine) {
        this.eventTime = logEntry.getTimeOfEvent();
        this.safepointCause = SafepointCause.valueOf(logEntry.getGroup(1));
    }

    private void leavingSafepointRegion(SafepointLogEntry logEntry, String logLine) {
        currentTime = logEntry.getTimeOfEvent();
    }

    private void safepointDetails(SafepointLogEntry logEntry, String logLine) {
        record(new Safepoint(eventTime,safepointCause,logEntry.getDoubleGroup(3),logEntry.getDoubleGroup(1)));
    }

    private void jvmTermination(SafepointLogEntry logEntry, String logLine) {
        record(new JVMTermination(logEntry.getTimeOfEvent()));
    }

    private void record(JVMEvent event) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(event);
            outbox.offer(event);
            System.out.println(baos.size());
        } catch (IOException ioe) {}
        eventTime = -1.0d;
        safepointCause = null;
    }

    @Override
    public void accept(String line) {
        this.parse(line);
    }
}
