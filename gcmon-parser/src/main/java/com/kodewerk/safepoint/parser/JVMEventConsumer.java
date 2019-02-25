package com.kodewerk.safepoint.parser;

import com.kodewerk.safepoint.event.JVMEvent;

public interface JVMEventConsumer {

    void offer(JVMEvent event);

}
