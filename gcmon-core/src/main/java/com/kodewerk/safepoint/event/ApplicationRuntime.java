package com.kodewerk.safepoint.event;

public class ApplicationRuntime extends JVMEvent {

    public ApplicationRuntime(String sessionID, double timeOfEvent, double duration) {
        super(sessionID, timeOfEvent, duration);
    }

    public void execute(EventConsumer eventConsumer) {
        eventConsumer.accept(this);
    }

    public String toString() {
        return "Application Runtime";
    }
}
