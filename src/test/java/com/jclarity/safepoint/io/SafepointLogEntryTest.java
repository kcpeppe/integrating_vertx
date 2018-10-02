package com.jclarity.safepoint.io;

import com.jclarity.safepoint.parser.SafepointParseRule;
import org.junit.jupiter.api.Test;

import static com.jclarity.safepoint.parser.Tokens.TIME_STAMP;
import static org.junit.jupiter.api.Assertions.*;

public class SafepointLogEntryTest {

    private static SafepointParseRule testRule = new SafepointParseRule("Test Rule", "Test: " + TIME_STAMP + ", " + TIME_STAMP);
    private static String goodLogLine = "[0.691s][info][safepoint    ] Test: 0.0000636 seconds, 0.0000187 seconds";
    private static String badLogLine = "[0.691s][info][safepoint    ] Test: ";

    @Test
    public void testGoodSafepointLogEntry() {
        SafepointLogEntry dummyEntry = testRule.parse(goodLogLine);
        assertTrue(dummyEntry.matched(),"we didn't get the expected match");
        assertEquals(0.0000636, dummyEntry.getDoubleGroup(1), "first double group mismatch");
        assertEquals( 0.0000187, dummyEntry.getDuration(), "duration mismatch");
        assertEquals( 0.0000187, dummyEntry.getDoubleGroup(3), "second double group mismatch");
    }

    @Test
    public void testBadSafepointLogEntry() {
        SafepointLogEntry dummyEntry = testRule.parse(badLogLine);
        assertFalse(dummyEntry.matched(),"we got an unexpected match");
    }
}
