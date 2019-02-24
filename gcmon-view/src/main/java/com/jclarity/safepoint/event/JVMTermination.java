package com.kodewerk.safepoint.event;

import com.kodewerk.safepoint.aggregator.Aggregator;

public class JVMTermination extends JVMEvent {

    public JVMTermination(double timeOfEvent) {
        super(timeOfEvent,0.0d);
    }

    public void execute(Aggregator aggregator) {
        aggregator.record(this);
    }

    public String toString() {
        return "JVMTermination";
    }
}
