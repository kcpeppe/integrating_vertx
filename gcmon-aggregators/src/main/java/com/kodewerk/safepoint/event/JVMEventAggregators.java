package com.kodewerk.safepoint.event;

import com.kodewerk.safepoint.aggregator.AggregatorSet;
import com.kodewerk.safepoint.aggregator.ApplicationRuntimeSummary;
import com.kodewerk.safepoint.aggregator.SafepointSummary;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;

import java.util.concurrent.ConcurrentHashMap;

public class JVMEventAggregators extends AbstractVerticle {

    private String inbox;
    private String outbox;
    private ConcurrentHashMap<String,AggregatorSet> sessions = new ConcurrentHashMap();
    private DeliveryOptions applicationRuntimeSummaryCodecName = new DeliveryOptions().setCodecName("ApplicationRuntimeSummary");
    private DeliveryOptions safepointSummaryCodecName = new DeliveryOptions().setCodecName("SafepointSummary");

    public JVMEventAggregators(String inbox, String outbox) {
        this.inbox = inbox;
        this.outbox = outbox;
    }

    private AggregatorSet newAggregators() {
        AggregatorSet aggregators = new AggregatorSet();
        aggregators.addAggregator(new ApplicationRuntimeSummary());
        aggregators.addAggregator(new SafepointSummary());
        return aggregators;
    }

    public void record(String sessionID) {
        try {
            sessions.get(sessionID).
                    forEach(aggregator -> {
                        DeliveryOptions options = (aggregator.getClass().getName().contains("Application")) ? applicationRuntimeSummaryCodecName : safepointSummaryCodecName;
                        vertx.eventBus().publish(outbox, aggregator, options);
                    });
            sessions.remove(sessionID);
        } catch (Exception t) {
            System.out.println(t.getMessage());
            t.printStackTrace();
        }
    }

    @Override
    public void start(Future<Void> future) {
        vertx.eventBus().
                <JVMEvent>consumer(inbox, message -> {
                    try {
                        JVMEvent event = message.body();
                        if (event instanceof JVMStart) {
                            sessions.put(event.getSessionID(), newAggregators());
                        } else {
                            sessions.get(event.getSessionID()).accept(event);
                            if (event instanceof JVMTermination) {
                                record(event.getSessionID()); //bad hack
                            }
                        }
                    } catch (Exception t) {
                        System.out.println(t.getMessage());
                        t.printStackTrace();
                    }
                })
                .completionHandler(v -> future.complete());
    }

}
