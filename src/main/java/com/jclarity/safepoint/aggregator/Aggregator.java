package com.jclarity.safepoint.aggregator;

import com.jclarity.safepoint.event.ApplicationRuntime;
import com.jclarity.safepoint.event.JVMTermination;
import com.jclarity.safepoint.event.Safepoint;

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
