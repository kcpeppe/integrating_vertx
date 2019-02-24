package com.kodewerk.safepoint.event;

import com.kodewerk.safepoint.aggregator.Aggregator;

public class ApplicationRuntime extends JVMEvent {

    public ApplicationRuntime(double timeOfEvent, double duration) {
        super(timeOfEvent,duration);
    }

    public void execute(Aggregator aggregator) {
        aggregator.record(this);
    }

    public String toString() {
        return "Application Runtime";
    }
}
