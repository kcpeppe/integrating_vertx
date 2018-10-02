package com.jclarity.safepoint.event;

public class Safepoint extends JVMEvent {

    private final SafepointCause safepointCause;
    private final double timeToSafepoint;
    public Safepoint(double timeOfEvent, SafepointCause cause, double ttsp, double duration) {
        super( timeOfEvent, duration);
        this.safepointCause = cause;
        this.timeToSafepoint = ttsp;
    }

    public SafepointCause getSafepointCause() {
        return safepointCause;
    }

    public double getTimeToSafepoint() {
        return timeToSafepoint;
    }
}
