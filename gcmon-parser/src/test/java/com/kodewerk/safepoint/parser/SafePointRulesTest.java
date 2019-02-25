package com.kodewerk.safepoint.parser;

import com.kodewerk.safepoint.event.SafepointCause;
import com.kodewerk.safepoint.io.SafepointLogEntry;
import org.junit.jupiter.api.Test;

import static com.kodewerk.safepoint.parser.SafepointRules.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SafePointRulesTest {

    @Test
    public void testApplicationTimeRule() {
        SafepointLogEntry entry = APPLICATION_TIME.parse("[0.691s][info][safepoint    ] Application time: 0.0008500 seconds");
        assertTrue(entry.matched(),"rule didn't capture log entry");
        assertEquals(0.691d, entry.getTimeOfEvent(), "Event time mismatch");
        assertEquals(entry.getDuration(), 0.0008500d, "duration mismatch");
    }

    @Test
    public void testEnteringSafepointRegion() {
        SafepointLogEntry entry = ENTERING_SAFEPOINT_REGION.parse("[0.691s][info][safepoint    ] Entering safepoint region: RevokeBias");
        assertTrue(entry.matched(),"rule didn't capture log entry");
        assertEquals(0.691d, entry.getTimeOfEvent(), "time of event mismatch");
        assertEquals(SafepointCause.RevokeBias, SafepointCause.valueOf(entry.getGroup(1)), "SafepointView reason mismatch");
    }

    @Test
    public void testLeavingSafepointRegion() {
        SafepointLogEntry entry = LEAVING_SAFEPOINT_REGION.parse("[0.692s][info][safepoint    ] Leaving safepoint region");
        assertTrue(entry.matched(),"rule didn't capture log entry");
        assertEquals(0.692, entry.getTimeOfEvent(), "event time mis-match");
    }

    @Test
    public void testLeavingSafepointDetails() {
        SafepointLogEntry entry = SAFEPOINT_DETAILS.parse("[0.692s][info][safepoint    ] Total time for which application threads were stopped: 0.0000636 seconds, Stopping threads took: 0.0000187 seconds");
        assertTrue(entry.matched(),"rule didn't capture log entry");
        assertEquals(0.692d, entry.getTimeOfEvent(), "time of event mismatch");
        assertEquals(0.0000636d, entry.getDoubleGroup(1), "duration mis-match");
        assertEquals(0.0000187d, entry.getDoubleGroup(3), "TTSP mis-match");
    }
}
