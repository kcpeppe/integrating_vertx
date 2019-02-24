package com.kodewerk.safepoint.event;

import com.kodewerk.safepoint.aggregator.AggregatorSet;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;

public class JVMEventAggregators extends AbstractVerticle {

    private String inbox;
    private AggregatorSet aggregators;
    private DeliveryOptions options = new DeliveryOptions().setCodecName("JVMEvent");

    public JVMEventAggregators(String inbox, AggregatorSet aggregators) {
        this.inbox = inbox;
        this.aggregators = aggregators;
    }

    @Override
    public void start(Future<Void> future) {
        vertx.eventBus().
                <JVMEvent>consumer(inbox, message -> {
                    try {
                        aggregators.accept(message.body());
                    } catch (Exception t) {
                        System.out.println(t.getMessage());
                    }
                })
                .completionHandler(v -> future.complete());
    }

}
