package com.kodewerk.safepoint.aggregator;

import com.kodewerk.safepoint.event.ApplicationRuntime;
import com.kodewerk.safepoint.event.EventConsumer;
import com.kodewerk.safepoint.event.JVMTermination;
import com.kodewerk.safepoint.event.Safepoint;

public abstract class Aggregator implements EventConsumer {

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

    public void accept(Safepoint safepoint) {}

    public void accept(ApplicationRuntime applicationRuntime) {}

    public void accept(JVMTermination termination) {}

}
