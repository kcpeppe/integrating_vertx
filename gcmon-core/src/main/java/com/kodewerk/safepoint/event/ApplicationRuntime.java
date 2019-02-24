package com.kodewerk.safepoint.event;

public class ApplicationRuntime extends JVMEvent {

    public ApplicationRuntime(double timeOfEvent, double duration) {
        super(timeOfEvent,duration);
    }

    public void execute(EventConsumer eventConsumer) {
        eventConsumer.accept(this);
    }

    public String toString() {
        return "Application Runtime";
    }
}
