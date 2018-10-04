package com.jclarity.safepoint.aggregator;

import com.jclarity.safepoint.event.ApplicationRuntime;
import com.jclarity.safepoint.event.Safepoint;
import com.jclarity.safepoint.event.SafepointCause;

import java.util.ArrayList;
import java.util.HashMap;

public class SafepointSummary extends Aggregator {

    private double totalPauseTime = 0.0d;
    private double longestPause = 0.0d;
    private double ttsp = 0.0d;
    private double longestTTSP = 0.0d;
    private HashMap<SafepointCause,ArrayList<DataPoint>> pauseTimeSeries = new HashMap<>();
    private HashMap<SafepointCause,ArrayList<DataPoint>> ttspTimeSeries = new HashMap<>();
    public HashMap<SafepointCause,Integer> safepointCauseCounts = new HashMap<>();

    public HashMap<SafepointCause,Integer> getSafepointCauseCounts() { return safepointCauseCounts; }
    public HashMap<SafepointCause,ArrayList<DataPoint>> getPauseTimeSeries() { return pauseTimeSeries; }
    public HashMap<SafepointCause,ArrayList<DataPoint>> getTtspTimeSeries() { return ttspTimeSeries; }
    public double getTotalPauseTime() { return totalPauseTime; }
    public double getLongestPause() { return longestPause; }
    public double getTtsp() { return ttsp; }
    public double getLongestTTSP() { return longestTTSP; }

    private void countSafepointCause( SafepointCause cause) {
        int value = safepointCauseCounts.computeIfAbsent(cause, aCause -> 0);
        safepointCauseCounts.put(cause,value + 1);
    }

    private void add(HashMap<SafepointCause,ArrayList<DataPoint>> map, SafepointCause cause, double timeStamp, double value) {
        ArrayList<DataPoint> timeSeries = map.computeIfAbsent(cause, aCause -> new ArrayList<>());
        timeSeries.add(new DataPoint(timeStamp,value));
    }

    public void record(ApplicationRuntime event) {
        recordTimeOfEvent(event.getEventTime());
    }

    public void record(Safepoint event) {
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
        String summary = "SafepointSummary\n----------------\nTotal Pause Time: " + getTotalPauseTime();
        summary += "\nlongest pause: " + getLongestPause();
        summary += "\nTotal TTSP: " + getTtsp();
        summary += "\nlongest TTSP: " + getLongestTTSP();
        HashMap<SafepointCause,Integer> causeCounts = getSafepointCauseCounts();
        summary += "\nCause:Count";
        for ( SafepointCause cause : causeCounts.keySet()) {
            summary += "\n" + cause + " : " + causeCounts.get(cause);
        }
        return summary + "\n================\n";
    }
}
