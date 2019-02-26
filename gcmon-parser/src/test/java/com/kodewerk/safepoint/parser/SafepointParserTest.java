package com.kodewerk.safepoint.parser;

import com.kodewerk.safepoint.event.ApplicationRuntime;
import com.kodewerk.safepoint.event.JVMEvent;
import com.kodewerk.safepoint.event.Safepoint;
import com.kodewerk.safepoint.io.SafepointLogLine;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SafepointParserTest {

    SafepointLogLine[] loglines = {
            new SafepointLogLine("Testing","[0.648s][info][safepoint    ] Application time: 0.0000172 seconds"),
            new SafepointLogLine("Testing","[0.648s][info][safepoint    ] Entering safepoint region: RevokeBias"),
            new SafepointLogLine("Testing","[0.648s][info][safepoint    ] Leaving safepoint region"),
            new SafepointLogLine("Testing","[0.648s][info][safepoint    ] Total time for which application threads were stopped: 0.0000313 seconds, Stopping threads took: 0.0000061 seconds")

    };
    @Test
    public void Test() {
        final ArrayList<JVMEvent> events = new ArrayList<>();
        SafepointParser parser = new SafepointParser(events::add);
        Arrays.stream(loglines).forEach(parser::parse);
        assertEquals(2,events.size());
        assertTrue(events.get(0) instanceof ApplicationRuntime);
        assertTrue(events.get(1) instanceof Safepoint);
    }
}
