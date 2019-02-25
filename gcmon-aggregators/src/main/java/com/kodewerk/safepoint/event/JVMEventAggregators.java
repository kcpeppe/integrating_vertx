package com.kodewerk.safepoint.event;

import com.kodewerk.safepoint.aggregator.AggregatorSet;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;

public class JVMEventAggregators extends AbstractVerticle {

    private String inbox;
    private String outbox;
    private AggregatorSet aggregators;
    private DeliveryOptions applicationRuntimeSummaryCodecName = new DeliveryOptions().setCodecName("ApplicationRuntimeSummary");
    private DeliveryOptions safepointSummaryCodecName = new DeliveryOptions().setCodecName("SafepointSummary");

    public JVMEventAggregators(String inbox, String outbox, AggregatorSet aggregators) {
        this.inbox = inbox;
        this.outbox = outbox;
        this.aggregators = aggregators;
    }

    public void record() {
        try {
            aggregators.forEach(aggregator -> {
                DeliveryOptions options = (aggregator.getClass().getName().contains("Application")) ? applicationRuntimeSummaryCodecName : safepointSummaryCodecName;
                vertx.eventBus().publish(outbox, aggregator, options);
            });
        } catch (Exception t) {
            System.out.println(t.getMessage());
        }
    }

    @Override
    public void start(Future<Void> future) {
        vertx.eventBus().
                <JVMEvent>consumer(inbox, message -> {
                    try {
                        aggregators.accept(message.body());
                        record(); //bad hack
                    } catch (Exception t) {
                        System.out.println(t.getMessage());
                    }
                })
                .completionHandler(v -> future.complete());
    }

}
