package com.kodewerk.safepoint.event;

public class JVMTermination extends JVMEvent {

    public static final String JVM_TERMINATION = "JVMTermination";

    public JVMTermination(String sessionID, double timeOfEvent) {
        super(sessionID, timeOfEvent,0.0d);
    }

    public void execute(EventConsumer eventConsumer) {
        eventConsumer.accept(this);
    }

    public String toString() {
        return JVM_TERMINATION;
    }
}
