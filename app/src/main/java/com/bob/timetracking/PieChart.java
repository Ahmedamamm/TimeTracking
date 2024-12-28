package com.bob.timetracking;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.eazegraph.lib.models.PieModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class PieChart extends AppCompatActivity {

    private org.eazegraph.lib.charts.PieChart pieChart;
    private ListView listView;
    private TextView from, to;
    private Database database;
    private LocalDate toDate = LocalDate.now();
    private LocalDate fromDate = LocalDate.now().minusDays(7);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM. yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_pie_chart);

        pieChart = findViewById(R.id.piechart);
        listView = findViewById(R.id.listview);
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);

        database = new Database(this);

        RefreshData();

        LinearLayout add = findViewById(R.id.add);
        add.setOnClickListener(v-> {
            Intent intent = new Intent(PieChart.this, TrackedActivityEditor.class);
            intent.putExtra("Operation", "Add");
            startActivity(intent);
        });

        from.setOnClickListener(v-> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
                fromDate = LocalDate.of(year, month+1, day);
                RefreshData();
            }, fromDate.getYear(), fromDate.getMonthValue()-1, fromDate.getDayOfMonth());
            datePickerDialog.show();
        });

        to.setOnClickListener(v-> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
                toDate = LocalDate.of(year, month+1, day);
                RefreshData();
            }, toDate.getYear(), toDate.getMonthValue()-1, toDate.getDayOfMonth());
            datePickerDialog.show();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshData();
    }

    private void RefreshData() {
        from.setText(formatter.format(fromDate));
        to.setText(formatter.format(toDate));
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
        ListAdapter listAdapter = new ListAdapter(activities);
        listView.setAdapter(listAdapter);
        configPieChart(activities);
    }

    private void configPieChart(ArrayList<TrackedActivity> activities) {
        pieChart.clearChart();
        for (TrackedActivity activity : activities) {
            pieChart.addPieSlice(new PieModel(activity.getTitle(), Math.round(activity.getTime()), Color.parseColor(activity.getColorCode())));
        }
        pieChart.startAnimation();
    }

    private class ListAdapter extends BaseAdapter {

        private final ArrayList<TrackedActivity> data;

        public ListAdapter(ArrayList<TrackedActivity> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public TrackedActivity getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            @SuppressLint({"InflateParams", "ViewHolder"})
            View v = getLayoutInflater().inflate(R.layout.pielistitem, null);
            TrackedActivity activity = data.get(i);
            View color = v.findViewById(R.id.color);
            TextView task = v.findViewById(R.id.task);
            color.setBackgroundColor(Color.parseColor(activity.getColorCode()));
            task.setText(activity.getTitle());
            v.setOnLongClickListener(v1-> {
                Intent intent = new Intent(PieChart.this, TrackedActivityEditor.class);
                intent.putExtra("Operation", "Update");
                intent.putExtra("ID", activity.getID());
                intent.putExtra("Title", activity.getTitle());
                intent.putExtra("Color", activity.getColorCode());
                intent.putExtra("Tags", activity.getTags());
                startActivity(intent);
                return true;
            });
            return v;
        }
    }

}