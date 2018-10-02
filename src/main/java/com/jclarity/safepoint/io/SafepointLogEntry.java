package com.jclarity.safepoint.io;

import java.util.regex.Matcher;

public class SafepointLogEntry {

    private final Matcher matcher;
    private final Decorators decorators;

    public SafepointLogEntry() {
        this(null,null);
    }

    public SafepointLogEntry(Decorators decorators, Matcher matcher) {
        this.matcher = matcher;
        this.decorators = decorators;
    }

    public boolean matched() {
        return matcher != null;
    }

    public String getGroup(int index) {
        return matcher.group(index);
    }

    public double getDuration() {
        return toDouble(matcher.group(matcher.groupCount() - 1));
    }

    public double getTimeOfEvent() {
        return decorators.getEventTime();
    }

    public double getDoubleGroup(int index) {
        return toDouble(matcher.group(index));
    }

    private double toDouble(String value) {
        return Double.parseDouble(value.replace(',', '.'));
    }
}
