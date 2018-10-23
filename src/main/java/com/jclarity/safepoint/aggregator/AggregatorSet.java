package com.jclarity.safepoint.aggregator;

import com.jclarity.safepoint.event.EventSink;
import com.jclarity.safepoint.event.JVMEvent;
import com.jclarity.safepoint.event.JVMTermination;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;

import java.util.ArrayList;

public class AggregatorSet extends AbstractVerticle implements EventSink<JVMEvent> {

    private final ArrayList<Aggregator> aggregators = new ArrayList<>();

    public AggregatorSet() {

    }

    public void addAggregator(Aggregator aggregator) {
        aggregators.add(aggregator);
    }

    public void addAggregators(Aggregator ... aggregator) {
        for (Aggregator anAggregator : aggregator)
            addAggregator(anAggregator);
    }

    @Override
    public void accept(JVMEvent event) {
        aggregators.forEach(event::execute);
    }

    private String inbox;

    public AggregatorSet(String inbox) {
        this.inbox = inbox;
    }

    @Override
    public void start(Future<Void> done) {
        // TODO Register a consumer on inbox (<JVMEvent>)

        // TODO set handler (process)

        // TODO Set completion handler
    }

    private void process(Message<JVMEvent> message) {
        try {
            JVMEvent event = message.body();
            if (event instanceof JVMTermination) {
                // Send the termination signal
                vertx.eventBus().publish("termination", "Done");
            } else {
                this.accept(event);
            }
        } catch (Exception t) {
            System.out.println(t.getMessage());
        }
    }
}
