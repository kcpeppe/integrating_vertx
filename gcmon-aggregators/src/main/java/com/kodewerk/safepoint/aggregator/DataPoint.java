package com.kodewerk.safepoint.aggregator;

import java.io.Serializable;

public class DataPoint implements Serializable {

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
