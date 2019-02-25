package com.kodewerk.safepoint.aggregator;

import com.kodewerk.safepoint.event.JVMEvent;

public interface AggregatorEventConsumer {

    void offer(Aggregator event);

}
