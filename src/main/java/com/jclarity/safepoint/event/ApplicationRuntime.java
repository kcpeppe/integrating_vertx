package com.jclarity.safepoint.event;

public class ApplicationRuntime extends JVMEvent {

    public ApplicationRuntime(double timeOfEvent, double duration) {
        super(timeOfEvent,duration);
    }
}
