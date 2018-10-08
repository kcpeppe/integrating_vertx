package com.jclarity.safepoint.io;

public class Decorators {

    private double eventTime;
    private String level;
    private String tag;

    public Decorators(String line) {
        String[] parts = line.split("\\]");
        eventTime = Double.parseDouble(parts[0].replace('[',' ').replace('s',' ').trim());
        level = parts[1].replace('[',' ').trim();
        tag = parts[2].replace('[',' ').trim();
    }

    public double getEventTime() {
        return this.eventTime;
    }

    public String getLevel() { return this.level; }
    public String getTag() { return this.tag; }
}
