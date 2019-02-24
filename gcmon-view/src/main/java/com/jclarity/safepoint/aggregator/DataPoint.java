package com.kodewerk.safepoint.aggregator;

public class DataPoint {

    private final double eventTime;
    private final double eventDuration;

    public DataPoint(double eventTime, double eventDuration) {
        this.eventTime = eventTime;
        this.eventDuration = eventDuration;
    }

    public double getEventTime() {
        return eventTime;
    }

    public double getEventDuration() {
        return eventDuration;
    }
}
