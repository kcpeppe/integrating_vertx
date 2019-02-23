package com.jclarity.safepoint.event;

import com.jclarity.safepoint.aggregator.Aggregator;

import java.io.Serializable;

public abstract class JVMEvent implements Serializable {

    private final double timeOfEvent;
    private final double duration;

    public JVMEvent(double timeOfEvent, double duration) {
        this.timeOfEvent = timeOfEvent;
        this.duration = duration;
    }

    public double getEventTime() { return this.timeOfEvent; }
    public double getDuration() { return this.duration; }

    public abstract void execute(Aggregator aggregator);

}
