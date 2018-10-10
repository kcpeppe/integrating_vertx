package com.jclarity.safepoint;

import com.jclarity.safepoint.aggregator.ApplicationRuntimeSummary;
import com.jclarity.safepoint.aggregator.SafepointSummary;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SafepointModelTest {

    @Test
    public void testSafepointModel() {
        SafepointEventBusModel model = new SafepointEventBusModel(new File("logs/safepoint.log").toPath());
        model.load();
        ApplicationRuntimeSummary runtimeSummary = model.getApplicationRuntimeSummary();
        SafepointSummary safepointSummary = model.getSafepointSummary();
        System.out.println(runtimeSummary.toString());
        System.out.println(safepointSummary.toString());
        assertEquals(0.01646d,safepointSummary.getLongestPause(), "Longest pause");
    }
}
