package com.kodewerk.safepoint.aggregator;

import com.kodewerk.safepoint.event.ApplicationRuntime;
import com.kodewerk.safepoint.event.JVMTermination;
import com.kodewerk.safepoint.event.Safepoint;
import com.kodewerk.safepoint.event.SafepointCause;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SafepointSummary extends Aggregator {

    private double totalPauseTime = 0.0d;
    private double longestPause = 0.0d;
    private double ttsp = 0.0d;
    private double longestTTSP = 0.0d;
    private HashMap<SafepointCause,ArrayList<DataPoint>> pauseTimeSeries = new HashMap<>();
    private HashMap<SafepointCause,ArrayList<DataPoint>> ttspTimeSeries = new HashMap<>();
    private Map<SafepointCause,Integer> safepointCauseCounts = new HashMap<>();

    public Map<SafepointCause,Integer> getSafepointCauseCounts() { return safepointCauseCounts; }
    public Map<SafepointCause,ArrayList<DataPoint>> getPauseTimeSeries() { return pauseTimeSeries; }
    public Map<SafepointCause,ArrayList<DataPoint>> getTtspTimeSeries() { return ttspTimeSeries; }
    public double getTotalPauseTime() { return totalPauseTime; }
    public double getLongestPause() { return longestPause; }
    public double getTtsp() { return ttsp; }
    public double getLongestTTSP() { return longestTTSP; }
    public int getNumberOfPauseEvents() {
        return pauseTimeSeries.values().stream().mapToInt(ArrayList::size).sum();
    }

    private void countSafepointCause( SafepointCause cause) {
        int value = safepointCauseCounts.computeIfAbsent(cause, aCause -> 0);
        safepointCauseCounts.put(cause,value + 1);
    }

    private void add(HashMap<SafepointCause,ArrayList<DataPoint>> map, SafepointCause cause, double timeStamp, double value) {
        ArrayList<DataPoint> timeSeries = map.computeIfAbsent(cause, aCause -> new ArrayList<>());
        timeSeries.add(new DataPoint(timeStamp,value));
    }

    public void accept(Safepoint event) {
        recordTimeOfEvent(event.getEventTime());
        countSafepointCause(event.getSafepointCause());
        add(pauseTimeSeries,event.getSafepointCause(), event.getEventTime(),event.getDuration());
        add(ttspTimeSeries,event.getSafepointCause(), event.getEventTime(),event.getTimeToSafepoint());
        totalPauseTime += event.getDuration();
        if ( event.getDuration() > longestPause)
            longestPause = event.getDuration();
        ttsp += event.getTimeToSafepoint();
        if ( event.getTimeToSafepoint() > longestTTSP)
            longestTTSP = event.getTimeToSafepoint();
    }

    public String toString() {
        StringBuilder summary = new StringBuilder("SafepointSummary\n----------------\nTotal Pause Time: " + getTotalPauseTime());
        summary.append("\nlongest pause: ").append(getLongestPause());
        summary.append("\nTotal TTSP: ").append(getTtsp());
        summary.append("\nlongest TTSP: ").append(getLongestTTSP());
        Map<SafepointCause,Integer> causeCounts = getSafepointCauseCounts();
        summary.append("\nCause:Count");
        for ( SafepointCause cause : causeCounts.keySet()) {
            summary.append("\n").append(cause).append(" : ").append(causeCounts.get(cause));
        }
        return summary + "\n================\n";
    }
}
