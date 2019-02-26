package com.kodewerk.safepoint.event;

public interface EventConsumer {

    void accept(Safepoint event);
    void accept(ApplicationRuntime event);
    void accept(JVMStart termination);
    void accept(JVMTermination termination);

}
