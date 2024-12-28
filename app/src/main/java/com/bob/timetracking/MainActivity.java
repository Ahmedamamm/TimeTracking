package com.bob.timetracking;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private PieChart pieChart;
    private GraphView graphView;
    private Database database;
    private TextView date;
    private TextView daysCounter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        pieChart = findViewById(R.id.piechart);
        graphView = findViewById(R.id.graph);
        date = findViewById(R.id.date);
        daysCounter = findViewById(R.id.dayscounter);
        database = new Database(this);

        pieChart.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, com.bob.timetracking.PieChart.class);
            startActivity(intent);
        });

        RelativeLayout modifyData = findViewById(R.id.modifyData);
        modifyData.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, ModifyData.class);
            startActivity(intent);
        });

        setupGraph(graphView);
        RefreshData();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        RefreshData();
    }

    private void setupGraph(GraphView graphView) {
        graphView.getGridLabelRenderer().setGridColor(Color.parseColor("#297575"));
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(5);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(6);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getViewport().setXAxisBoundsManual(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void RefreshData() {
        date.setText(DateTimeFormatter.ofPattern("EEE, d MMMM").format(LocalDate.now()));
        configPieChart();
        daysCounter.setText(new DecimalFormat("000").format(database.getAllRatings().length));
        getGraphData();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getGraphData() {
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = toDate.minusDays(7);
        int i=0;
        ArrayList<DataPoint> points = new ArrayList<>();
        for (LocalDate date=fromDate;date.isBefore(toDate) || date.isEqual(toDate);date=date.plusDays(1)) {
            points.add(new DataPoint(i, database.getRating(DateTimeFormatter.ofPattern("yyyy-dd-MM").format(date))));
            i++;
        }
        setDataPoints(points, graphView);
    }

    private void setDataPoints(ArrayList<DataPoint> points, GraphView graph) {
        int main = Color.parseColor("#297575");
        DataPoint[] dataPoints = new DataPoint[points.size()];
        for (int i=0;i<points.size();i++) {
            dataPoints[i] = points.get(i);
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        series.setDrawBackground(true);
        series.setColor(main);
        series.setBackgroundColor(main);
        series.setThickness(0);
        if (graph.getSeries().size()!=0) {
            for (Series s : graph.getSeries()) {
                graph.removeSeries(s);
            }
        }
        graph.addSeries(series);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configPieChart() {
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = toDate.minusDays(7);
        pieChart.clearChart();
        ArrayList<TrackedActivity> activities = database.getTrackedActivities();
        for (LocalDate date = fromDate;date.isBefore(toDate)||date.isEqual(toDate);date=date.plusDays(1)) {
            ArrayList<Activity> activities1 = database.getDay(DateTimeFormatter.ofPattern("yyyy-dd-MM").format(date));
            for (Activity a : activities1) {
                for (TrackedActivity b : activities) {
                    for (String s : b.getTags()) {
                        if (a.getActivity().toLowerCase().contains(s.toLowerCase())) {
                            b.setTime(b.getTime()+a.getTime());
                        }
                    }
                }
            }
        }
        for (TrackedActivity activity : activities) {
            pieChart.addPieSlice(new PieModel(activity.getTitle(), Math.round(activity.getTime()), Color.parseColor(activity.getColorCode())));
        }
        pieChart.startAnimation();
    }

}