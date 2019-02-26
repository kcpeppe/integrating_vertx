package com.kodewerk.safepoint.event;

import org.junit.jupiter.api.Test;

import static com.kodewerk.safepoint.event.SafepointCause.BulkRevokeBias;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SafepointTest {

    @Test
    public void testForProperlyConstructedSafepoint() {
        Safepoint safepoint = new Safepoint("Testing",1.0d ,BulkRevokeBias, 1.0d, 1.0d);
        assertEquals(1.0d, safepoint.getEventTime(), "event timestamp differs ");
        assertEquals(BulkRevokeBias, safepoint.getSafepointCause(), "event cause differs ");
        assertEquals(1.0d, safepoint.getTimeToSafepoint(), "event ttsp differs ");
        assertEquals(1.0d, safepoint.getDuration(), "event duration differs ");
    }
}
