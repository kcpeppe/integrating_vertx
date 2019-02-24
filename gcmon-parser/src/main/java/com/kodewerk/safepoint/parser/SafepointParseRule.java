package com.kodewerk.safepoint.parser;

import com.kodewerk.safepoint.io.Decorators;
import com.kodewerk.safepoint.io.SafepointLogEntry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SafepointParseRule {

    private final String name;
    private final Pattern pattern;

    public SafepointParseRule(String name, String regex) {
        this.name = name;
        this.pattern = Pattern.compile(regex);
    }

    public String getName() { return name; }

    public SafepointLogEntry parse(String trace) {
        Matcher matcher = pattern.matcher(trace);
        if (matcher.find()) {
            return new SafepointLogEntry(new Decorators(trace), matcher);
        } else {
            return new SafepointLogEntry();
        }
    }
}
