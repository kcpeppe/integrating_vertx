package com.kodewerk.safepoint.event;

public class JVMStart extends JVMEvent {

    public static final String JVM_START = "JVM Start";

    public JVMStart(String sessionID) {
        super(sessionID, 0.0d,0.0d);
    }

    public void execute(EventConsumer eventConsumer) {
        eventConsumer.accept(this);
    }

    public String toString() {
        return JVM_START;
    }
}
