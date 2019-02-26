package com.kodewerk.safepoint.io;

import java.io.Serializable;

public class SafepointLogLine implements Serializable {

    private String id;
    private String line;

    public SafepointLogLine(String id, String data) {
        this.id = id;
        this.line = data;
    }

    public String getId() { return id; }
    public String getLine() { return line; }
    public String toString() { return line; }
}
