package com.kodewerk.safepoint.parser;

import org.junit.jupiter.api.Test;

import static com.kodewerk.safepoint.io.SafepointLogFile.END_OF_FILE_TOKEN;
import static com.kodewerk.safepoint.parser.SafepointRules.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SafepointParseRuleTest {

    @Test
    public void testParseRules() {
        for ( int i = 0; i < rules.length; i++)
            for ( int j = 0; j < lines.length; j++) {
                int captured = captureTest( rules[i], lines[j]);
                if ( i == j) {
                    assertTrue( captured == lines[j].length, rules[i].getName() + " failed to captured it's lines");
                } else {
                    assertTrue( captured == 0, rules[i].getName() + " captured " + rules[j].getName());
                }
            }
        assertTrue( true);
    }

    private int captureTest( SafepointParseRule rule, String[] lines) {
        int captureCount = 0;
        for ( int i = 0; i < lines.length; i++)
            if ( rule.parse(lines[i]).matched())
                captureCount++;
        return captureCount;
    }

    SafepointParseRule[] rules = {
            APPLICATION_TIME,
            ENTERING_SAFEPOINT_REGION,
            LEAVING_SAFEPOINT_REGION,
            SAFEPOINT_DETAILS,
            JVM_TERMINATION
    };



    String[][] lines = {
        {"[0.648s][info][safepoint    ] Application time: 0.0000172 seconds"},
        {"[0.648s][info][safepoint    ] Entering safepoint region: RevokeBias"},
        {"[0.648s][info][safepoint    ] Leaving safepoint region"},
        {"[0.648s][info][safepoint    ] Total time for which application threads were stopped: 0.0000313 seconds, Stopping threads took: 0.0000061 seconds"},
        { END_OF_FILE_TOKEN}
    };

}
