package com.kodewerk.safepoint;

import com.kodewerk.safepoint.aggregator.ApplicationRuntimeSummary;
import com.kodewerk.safepoint.aggregator.DataPoint;
import com.kodewerk.safepoint.aggregator.SafepointSummary;
import com.kodewerk.safepoint.event.SafepointCause;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Map;

public abstract class SafepointSharedView extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Tabs");
        Group root = new Group();
        Scene scene = new Scene(root, 1200, 600, Color.WHITE);

        TabPane tabPane = new TabPane();
        openModel(tabPane);
        BorderPane borderPane = new BorderPane();
        // bind to take available space
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());

        borderPane.setCenter(tabPane);
        root.getChildren().add(borderPane);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    abstract void openModel(TabPane tabPane);

    void buildViews(TabPane tabPane, SafepointSummary safepointSummary, ApplicationRuntimeSummary applicationRuntimeSummary) {

        buildSafepointSummaryTab(tabPane, safepointSummary);

        ScatterChart<Number, Number> chart = buildScatterChart(
                "Safepoint durations",
                "Time (seconds)",
                "Duration (seconds)",
                safepointSummary.getPauseTimeSeries());
        addTab(tabPane, chart, "Safepoint durations");

        chart = buildScatterChart(
                "Time to Safepoint",
                "Time (seconds)",
                "Duration (seconds)",
                safepointSummary.getTtspTimeSeries());
        addTab(tabPane, chart, "Time to Safepoint");

        BarChart<Number, Number> barChart = buildBarChart(
                "Safepoint Causes",
                safepointSummary.getSafepointCauseCounts());
        addTab(tabPane, barChart, "Safepoint causes");

        chart = buildScatterChart(
                "Application Runtime",
                "Time (seconds)",
                "Runtime (seconds)",
                applicationRuntimeSummary.getRuntimeSeries());
        addTab(tabPane, chart, "Application runtime");

    }

    BarChart<Number, Number> buildBarChart(String title, Map<SafepointCause, Integer> safepointCauseCounts) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Safepoint causes");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Counts");

        BarChart barChart = new BarChart(xAxis,yAxis);
        barChart.setTitle(title);
        XYChart.Series dataSeries = new XYChart.Series();
        safepointCauseCounts.keySet().stream().forEach( key ->
                dataSeries.getData().add(new XYChart.Data(key.name(),safepointCauseCounts.get(key))));
        barChart.getData().add(dataSeries);
        return barChart;
    }

    XYChart.Series<Number,Number> buildXYChartSeries(String title, ArrayList<DataPoint> dataPoints) {
        XYChart.Series<Number,Number> series = new XYChart.Series<>();
        series.setName(title);
        dataPoints.stream().
                map(point -> new XYChart.Data<Number,Number>(point.getEventTime(),point.getEventDuration())).
                forEach(series.getData()::add);
        return series;
    }

    ScatterChart<Number,Number> buildScatterChart(String title, String xAxisLabel, String yAxisLabel, ArrayList<DataPoint> seriesData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        ScatterChart<Number,Number> chart = new ScatterChart<>(xAxis,yAxis);
        chart.setTitle(title);
        xAxis.setLabel(xAxisLabel);
        yAxis.setLabel(yAxisLabel);
        chart.getData().add(buildXYChartSeries("Application runtime", seriesData));
        return chart;
    }

    ScatterChart<Number,Number> buildScatterChart(String title, String xAxisLabel, String yAxisLabel, Map<SafepointCause,ArrayList<DataPoint>> seriesData) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(xAxisLabel);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);
        ScatterChart<Number,Number> chart = new ScatterChart<>(xAxis,yAxis);
        chart.setTitle(title);

        seriesData.keySet().forEach(key -> chart.getData().add(buildXYChartSeries(key.name(),seriesData.get(key))));
        return chart;
    }

    void addTab(TabPane tabPane, Chart chart, String title) {
        Tab tab = new Tab();
        tab.setText(title);
        HBox hbox = new HBox();
        hbox.getChildren().add(chart);
        chart.prefWidthProperty().bind(tabPane.widthProperty());
        chart.prefHeightProperty().bind(tabPane.heightProperty());
        hbox.setAlignment(Pos.CENTER);
        tab.setContent(hbox);
        tabPane.getTabs().add(tab);
    }




    void buildSafepointSummaryTab(TabPane tabPane, SafepointSummary summary) {

        ObservableList<Metric> data =
                FXCollections.observableArrayList(
                        new Metric( "Runtime", summary.getTimeOfLastEvent()),
                        new Metric("Number of pause events", summary.getNumberOfPauseEvents()),
                        new Metric( "Total pause time", summary.getTotalPauseTime()),
                        new Metric( "Longest pause", summary.getLongestPause()),
                        new Metric( "Pause %", 100.0d * summary.getTotalPauseTime() / summary.getTimeOfLastEvent()),

                        new Metric( "Total TTSP", summary.getTtsp()),
                        new Metric( "Longest TTSP", summary.getLongestTTSP()),
                        new Metric( "TTSP %", 100.0d * summary.getTtsp() / summary.getTimeOfLastEvent()),

                        new Metric( "TTSP:Pause", summary.getTtsp() / summary.getTotalPauseTime())
                );
        TableView table = new TableView();
        table.setEditable(false);
        table.setPrefWidth(400);

        TableColumn<Metric,String> metric = new TableColumn<>("Safepoint Summary");
        metric.setPrefWidth(200);
        metric.setCellValueFactory(new PropertyValueFactory<>("label"));

        TableColumn<Metric,String> value = new TableColumn<>("");
        value.setPrefWidth(200);
        value.setCellValueFactory(new PropertyValueFactory<>("measure"));

        table.getColumns().addAll(metric, value);

        table.getItems().setAll(data);

        Tab tab = new Tab();
        tab.setText("Safepoint Summary");
        VBox vbox = new VBox();
        vbox.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");
        vbox.getChildren().add(table);
        vbox.setAlignment(Pos.TOP_LEFT);
        tab.setContent(vbox);
        tabPane.getTabs().add(tab);
    }

    public class Metric {

        private SimpleStringProperty label;
        private SimpleStringProperty measure;

        Metric(String aLabel, Number value) {
            label = new SimpleStringProperty(aLabel);
            measure = new SimpleStringProperty(value.toString());

        }

        // found by reflection
        public StringProperty measureProperty() { return measure; }
        public StringProperty labelProperty() { return label; }
    }
}
