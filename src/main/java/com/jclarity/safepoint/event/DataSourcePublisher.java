package com.jclarity.safepoint.event;

import com.jclarity.safepoint.io.DataSource;

public interface DataSourcePublisher<T> {

    void publish(DataSource<T> dataSource);

    void awaitCompletion();

}
