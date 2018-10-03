package com.jclarity.safepoint.aggregator;

import com.jclarity.safepoint.event.ApplicationRuntime;
import com.jclarity.safepoint.event.Safepoint;

import java.util.ArrayList;

public class ApplicationRuntimeQuery {

    private double timeOfFirstEvent = Double.MAX_VALUE;
    private double timeOfLastEvent = 0.0d;

    private double totalRunTime = 0.0d;
    private ArrayList<DataPoint> runtimeSeries = new ArrayList<>();

    private void recordTimeOfEvent(double eventTime) {
        if ( eventTime < timeOfFirstEvent)
            timeOfFirstEvent = eventTime;
        if ( eventTime > timeOfLastEvent)
            timeOfLastEvent = eventTime;
    }

    public void record(ApplicationRuntime event) {
        recordTimeOfEvent(event.getEventTime());
        runtimeSeries.add( new DataPoint(event.getEventTime(), event.getDuration()));
        totalRunTime += event.getDuration();
    }

    public void record(Safepoint event) {
        recordTimeOfEvent(event.getEventTime());
    }
}
