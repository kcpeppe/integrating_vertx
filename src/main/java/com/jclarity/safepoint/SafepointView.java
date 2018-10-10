package com.jclarity.safepoint;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.TabPane;

import java.io.File;

public class SafepointView extends SafepointSharedView {

    public static void main(String[] args) {
        launch();
    }

    SafepointEventBusModel model;
    void openModel(TabPane tabPane) {
        model = new SafepointEventBusModel(new File("./logs/safepoint.log").toPath());
        model.load();
        buildViews(tabPane, model.getSafepointSummary(), model.getApplicationRuntimeSummary());

//        buildSafepointSummaryTab(tabPane, model.getSafepointSummary());
//
//        ScatterChart<Number,Number> chart = buildScatterChart(
//                "Safepoint durations",
//                "Time (seconds)",
//                "Duration (seconds)",
//                model.getSafepointSummary().getPauseTimeSeries());
//        addTab(tabPane, chart, "Safepoint durations");
//
//        chart = buildScatterChart(
//                "Time to Safepoint",
//                "Time (seconds)",
//                "Duration (seconds)",
//                model.getSafepointSummary().getTtspTimeSeries());
//        addTab(tabPane, chart, "Time to Safepoint");
//
//        BarChart<Number,Number> barChart = buildBarChart(
//                "Safepoint Causes",
//                model.getSafepointSummary().getSafepointCauseCounts());
//        addTab(tabPane, barChart, "Safepoint causes");
//
//        chart = buildScatterChart(
//                "Application Runtime",
//                "Time (seconds)",
//                "Runtime (seconds)",
//                model.getApplicationRuntimeSummary().getRuntimeSeries());
//        addTab(tabPane, chart, "Application runtime");
    }
}
