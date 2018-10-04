package com.jclarity.safepoint.event;

import com.jclarity.safepoint.aggregator.Aggregator;

public class JVMTermination extends JVMEvent {

    public JVMTermination(double timeOfEvent) {
        super(timeOfEvent,0.0d);
    }

    public void execute(Aggregator aggregator) {
        aggregator.record(this);
    }
}
