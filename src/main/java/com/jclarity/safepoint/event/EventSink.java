package com.jclarity.safepoint.event;

public interface EventSink<T> {

    void accept(T event);

}
