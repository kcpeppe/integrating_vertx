package com.jclarity.safepoint.parser;

import com.jclarity.safepoint.event.ApplicationRuntime;
import com.jclarity.safepoint.event.JVMEvent;
import com.jclarity.safepoint.event.JVMTermination;
import com.jclarity.safepoint.event.Safepoint;
import com.jclarity.safepoint.event.SafepointCause;
import com.jclarity.safepoint.io.SafepointLogEntry;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import static com.jclarity.safepoint.parser.SafepointRules.*;

public class SafepointParser {

    private static final Logger LOGGER = Logger.getLogger(SafepointParser.class.getName());

    private EventConsumer eventConsumer;
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

    public SafepointParser(EventConsumer eventConsumer) {
        this.eventConsumer = eventConsumer;
    }

    public void parse(String line) {
        Optional<AbstractMap.SimpleEntry<SafepointParseRule, SafepointLogEntry>> ruleToApply = parseRules.keySet().stream()
                .map(rule -> new AbstractMap.SimpleEntry<>(rule, rule.parse(line)))
                .filter(tuple -> tuple.getValue() != null)
                .findFirst();

        try {
            if (ruleToApply.isPresent() && ruleToApply.get().getValue().matched()) {
                parseRules.get(ruleToApply.get().getKey()).accept(ruleToApply.get().getValue(), line);
            } else {
                LOGGER.warning("Missed: " + line);
            }
        } catch (Throwable t) {
            LOGGER.throwing(this.getClass().getName(), "process", t);
        }
    }

    private void applicationTime(SafepointLogEntry logEntry, String logLine) {
        record( new ApplicationRuntime(logEntry.getTimeOfEvent(),logEntry.getDuration()));
    }

    private void enteringSafepointRegion(SafepointLogEntry logEntry, String logLine) {
        this.eventTime = logEntry.getTimeOfEvent();
        this.safepointCause = SafepointCause.valueOf(logEntry.getGroup(2));
    }

    private void leavingSafepointRegion(SafepointLogEntry logEntry, String logLine) {
        currentTime = logEntry.getTimeOfEvent();
        noop();
    }

    private void safepointDetails(SafepointLogEntry logEntry, String logLine) {
        record(new Safepoint(eventTime,safepointCause,logEntry.getDoubleGroup(3),logEntry.getDoubleGroup(1)));
    }

    private void jvmTermination(SafepointLogEntry logEntry, String logLine) {
        record(new JVMTermination(logEntry.getTimeOfEvent()));
    }

    private void record(JVMEvent event) {
        eventConsumer.offer(event);
        eventTime = -1.0d;
        safepointCause = null;
    }

    private void noop() {}

}
