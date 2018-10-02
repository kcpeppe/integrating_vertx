package com.jclarity.safepoint.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JVMTerminationTest {

    @Test
    public void testForProperlyConstructedJVMTermination() {
        JVMTermination jvmTermination = new JVMTermination(1.0);
        assertEquals(1.0d, jvmTermination.getTimeOfEvent(), "event timestamp differs ");
        assertEquals(0.0d, jvmTermination.getDuration(), "event duration differs ");
    }
}
