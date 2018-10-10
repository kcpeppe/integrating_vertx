package com.jclarity.safepoint.aggregator;

import com.jclarity.safepoint.event.EventSink;
import com.jclarity.safepoint.event.JVMEvent;
import com.jclarity.safepoint.event.JVMTermination;
import io.vertx.core.AbstractVerticle;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

//public class AggregatorSet implements EventSink<JVMEvent> {
public class AggregatorSet extends AbstractVerticle implements EventSink<JVMEvent> {

    private final ArrayList<Aggregator> aggregators = new ArrayList<>();

    public AggregatorSet() {

    }

    public void addAggregator(Aggregator aggregator) {
        aggregators.add(aggregator);
    }

    @Override
    public void accept(JVMEvent event) {
        aggregators.stream().forEach(event::execute);
    }

    //Vert.x
    private CountDownLatch deployed = new CountDownLatch(1);
    private CountDownLatch completed = new CountDownLatch(1);

    private String inbox;

    public AggregatorSet(String inbox) {
        this.inbox = inbox;
    }

    public void awaitDeployment() {
        try {
            deployed.await();
        } catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
    }

    public void awaitCompletion() {
        try {
            completed.await();
        } catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
    }

    private void processTerminationEvent() {
        completed.countDown();
    }

    @Override
    public void start() {
        try {
            vertx.eventBus().
                    consumer(inbox, message -> {
                        try {
                            JVMEvent event = (JVMEvent)message.body();
                            if ( ! (event instanceof JVMTermination)) {
                                this.accept(event);
                            } else {
                                processTerminationEvent();
                            }
                        } catch (Throwable t) {
                            System.out.println(t.getMessage());
                        }
                    });
            deployed.countDown();
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }
    }
}
