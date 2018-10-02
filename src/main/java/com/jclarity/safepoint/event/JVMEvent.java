package com.jclarity.safepoint.event;

public abstract class JVMEvent {

    private final double timeOfEvent;
    private final double duration;

    public JVMEvent(double timeOfEvent, double duration) {
        this.timeOfEvent = timeOfEvent;
        this.duration = duration;
    }

    public double getTimeOfEvent() { return this.timeOfEvent; }
    public double getDuration() { return this.duration; }
}
