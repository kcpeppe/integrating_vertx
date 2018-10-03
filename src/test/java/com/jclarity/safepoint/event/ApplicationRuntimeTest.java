package com.jclarity.safepoint.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationRuntimeTest {

    @Test
    public void testForProperlyConstructedApplicationRuntime() {
        ApplicationRuntime applicationRuntime = new ApplicationRuntime(1.0,2.0);
        assertEquals(1.0d, applicationRuntime.getEventTime(), "event timestamp differs ");
        assertEquals(2.0d, applicationRuntime.getDuration(), "event duration differs ");
    }
}
