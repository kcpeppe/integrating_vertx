package com.jclarity.safepoint.event;

public class JVMTermination extends JVMEvent {
    public JVMTermination(double timeOfEvent) {
        super(timeOfEvent,0.0d);
    }
}
