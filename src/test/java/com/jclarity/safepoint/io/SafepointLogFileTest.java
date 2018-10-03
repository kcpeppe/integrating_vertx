package com.jclarity.safepoint.io;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SafepointLogFileTest {

    @Test
    public void testReadingSafepointLogFile() {
        try {
            new File("data/safepoint.log").toPath();
            SafepointLogFile logfile = new SafepointLogFile(new File("logs/safepoint.log").toPath());
            long count = logfile.stream().count();
            assertEquals(5273L, count, "all lines not processed. ");
        } catch(IOException ioe) {
            fail(ioe.getMessage());
        }
    }
}
