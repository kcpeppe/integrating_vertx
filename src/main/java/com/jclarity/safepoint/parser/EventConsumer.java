package com.jclarity.safepoint.parser;

import com.jclarity.safepoint.event.JVMEvent;

public interface EventConsumer {

    void offer(JVMEvent event);

}
