package com.jclarity.safepoint.aggregator;

import com.jclarity.safepoint.event.ApplicationRuntime;
import com.jclarity.safepoint.event.Safepoint;
import com.jclarity.safepoint.event.SafepointCause;

import java.util.ArrayList;
import java.util.HashMap;

public class SafepointSummary {

    private double timeOfFirstEvent = Double.MAX_VALUE;
    private double timeOfLastEvent = 0.0d;

    private double totalPauseTime = 0.0d;
    private HashMap<SafepointCause,ArrayList<DataPoint>> pauseTimeSeries = new HashMap<>();

    private void recordTimeOfEvent(double eventTime) {
        if ( eventTime < timeOfFirstEvent)
            timeOfFirstEvent = eventTime;
        if ( eventTime > timeOfLastEvent)
            timeOfLastEvent = eventTime;
    }

    public void record(ApplicationRuntime event) {
        recordTimeOfEvent(event.getEventTime());
    }

    public void record(Safepoint event) {
        recordTimeOfEvent(event.getEventTime());
        totalPauseTime += event.getDuration();
        // record pause time by cause
        // record ttsp
        // count safepoint cause events (could be find lengths of pause arrays)
    }
}
