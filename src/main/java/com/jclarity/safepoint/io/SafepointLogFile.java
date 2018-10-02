package com.jclarity.safepoint.io;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class SafepointLogFile {

    private boolean eof = false;
    CopyOnWriteArrayList<SafepointLogEntry> logEntries;

    public SafepointLogFile() {}

    public boolean isEof() {
        return false;
    }

    public void record(SafepointLogEntry entry) {

    }

    public Stream<SafepointLogEntry> stream() {
        return logEntries.stream();
    }
}
