package com.jclarity.safepoint.aggregator;

import com.jclarity.safepoint.event.EventSink;
import com.jclarity.safepoint.event.JVMEvent;

import java.util.ArrayList;

public class AggregatorSet implements EventSink<JVMEvent> {

    private final ArrayList<Aggregator> aggregators = new ArrayList<>();

    public AggregatorSet() {

    }

    public void addAggregator(Aggregator aggregator) {
        aggregators.add(aggregator);
    }

    @Override
    public void accept(JVMEvent event) {
        aggregators.forEach(event::execute);
    }
}
