package com.kodewerk.safepoint.io;

public class Decorators {

    private double eventTime = 0.0d;
    private String level = "";
    private String tag = "";

    public Decorators(String line) {
        String[] parts = line.split("\\]");
        if (parts.length == 4) {
            eventTime = Double.parseDouble(parts[0].replace('[', ' ').replace('s', ' ').trim());
            level = parts[1].replace('[', ' ').trim();
            tag = parts[2].replace('[', ' ').trim();
        }
    }

    public double getEventTime() {
        return this.eventTime;
    }

    public String getLevel() { return this.level; }
    public String getTag() { return this.tag; }
}
