package com.kodewerk.safepoint.aggregator;

import com.kodewerk.safepoint.event.ApplicationRuntime;
import com.kodewerk.safepoint.event.JVMTermination;
import com.kodewerk.safepoint.event.Safepoint;

public class Aggregator {

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

    public void record(Safepoint safepoint) {}

    public void record(ApplicationRuntime applicationRuntime) {}

    public void record(JVMTermination termination) {}
}
