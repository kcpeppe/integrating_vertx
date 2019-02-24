package com.kodewerk.safepoint.event;

public interface EventConsumer {

    void accept(JVMEvent event);

}
