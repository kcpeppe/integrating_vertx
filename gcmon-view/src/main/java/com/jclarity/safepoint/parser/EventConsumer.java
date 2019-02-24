package com.kodewerk.safepoint.parser;

import com.kodewerk.safepoint.event.JVMEvent;

public interface EventConsumer {

    void offer(JVMEvent event);

}
