package com.jclarity.safepoint.event;

import com.jclarity.safepoint.io.DataSource;

import java.io.IOException;

public class EventSource<T> {

    private DataSource<T> dataSource;
    private EventBus<T> eventBus;

    public EventSource(DataSource<T> dataSource, EventBus<T> eventBus) {
        this.dataSource = dataSource;
        this.eventBus = eventBus;
    }

    public void stream() throws IOException {
        dataSource.stream().forEach(eventBus::publish);
    }
}
