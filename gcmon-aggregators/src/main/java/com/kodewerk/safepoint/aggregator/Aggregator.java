package com.kodewerk.safepoint.aggregator;

import com.kodewerk.safepoint.event.*;

import java.io.Serializable;

public abstract class Aggregator implements EventConsumer, Serializable {

    private double timeOfFirstEvent = Double.MAX_VALUE;
    private double timeOfLastEvent = 0.0d;

    void recordTimeOfEvent(double eventTime) {
        if ( eventTime < timeOfFirstEvent)
            timeOfFirstEvent = eventTime;
        if ( eventTime > timeOfLastEvent)
            timeOfLastEvent = eventTime;
    }

    public double getTimeOfFirstEvent() { return this.timeOfFirstEvent; }
    public double getTimeOfLastEvent() { return  this.timeOfLastEvent; }

    public void accept(Safepoint safepointEvent) {}

    public void accept(ApplicationRuntime applicationRuntimeEvent) {}

    public void accept(JVMTermination terminationEvent) {}

    public void accept(JVMStart startEvent) {}

    public abstract String toString();

}
