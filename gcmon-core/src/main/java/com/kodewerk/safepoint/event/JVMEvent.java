package com.kodewerk.safepoint.event;

import java.io.Serializable;

public abstract class JVMEvent implements Serializable {

    private final double timeOfEvent;
    private final double duration;
    private String sessionID;

    public JVMEvent(String sessionID, double timeOfEvent, double duration) {
        this.sessionID = sessionID;
        this.timeOfEvent = timeOfEvent;
        this.duration = duration;
    }

    public String getSessionID() { return sessionID;}
    public double getEventTime() { return this.timeOfEvent; }
    public double getDuration() { return this.duration; }

    public abstract void execute(EventConsumer eventConsumer);

}
