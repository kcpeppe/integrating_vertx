package com.kodewerk.safepoint.event;

import com.kodewerk.safepoint.io.DataSource;

public interface DataSourcePublisher<T> {

    void publish(DataSource<T> dataSource);

    void awaitCompletion();

}
