package com.kodewerk.safepoint.aggregator;

import com.kodewerk.safepoint.event.ApplicationRuntime;
import com.kodewerk.safepoint.event.Safepoint;

import java.util.ArrayList;

public class ApplicationRuntimeSummary extends Aggregator {

    private double totalRunTime = 0.0d;
    private ArrayList<DataPoint> runtimeSeries = new ArrayList<>();

    public double getTotalRunTime() { return totalRunTime; }
    public ArrayList<DataPoint> getRuntimeSeries() { return runtimeSeries; }

    public void accept(ApplicationRuntime event) {
        recordTimeOfEvent(event.getEventTime());
        runtimeSeries.add( new DataPoint(event.getEventTime(), event.getDuration()));
        totalRunTime += event.getDuration();
    }

    public void record(Safepoint event) {
        recordTimeOfEvent(event.getEventTime());
    }

    public String toString() {
        return "Application Runtime Summary\n---------------------------\nTotal Runtime: "
                + getTotalRunTime()
                + "\nNumber of events: "
                + getRuntimeSeries().size()
                + "\n===========================\n";
    }
}
