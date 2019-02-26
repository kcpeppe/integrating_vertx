package com.kodewerk.safepoint.event;

public class Safepoint extends JVMEvent {

    private final SafepointCause safepointCause;
    private final double timeToSafepoint;
    public Safepoint(String sessionID, double timeOfEvent, SafepointCause cause, double ttsp, double duration) {
        super( sessionID, timeOfEvent, duration);
        this.safepointCause = cause;
        this.timeToSafepoint = ttsp;
    }

    public SafepointCause getSafepointCause() {
        return safepointCause;
    }

    public double getTimeToSafepoint() {
        return timeToSafepoint;
    }

    public void execute(EventConsumer eventConsumer) {
        eventConsumer.accept(this);
    }

    public String toString() {
        return "Safepoint";
    }
}
