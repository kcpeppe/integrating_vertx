package com.kodewerk.safepoint.aggregator;

import com.kodewerk.safepoint.event.JVMEvent;

import java.util.ArrayList;
import java.util.function.Consumer;

public class AggregatorSet {

    private final ArrayList<Aggregator> aggregators = new ArrayList<>();

    public AggregatorSet() {}


    public void addAggregator(Aggregator aggregator) {
        aggregators.add(aggregator);
    }

    public void addAggregators(Aggregator ... aggregator) {
        for (Aggregator anAggregator : aggregator)
            addAggregator(anAggregator);
    }

    public void accept(JVMEvent event) {
        aggregators.forEach(event::execute);
    }

    public void forEach(Consumer<Aggregator> action) {
        aggregators.forEach(action);
    }
}
