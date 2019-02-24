package com.kodewerk.safepoint.event;

public interface EventSink<T> {

    void accept(T event);

}
