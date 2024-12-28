package com.bob.timetracking;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ModifyData extends AppCompatActivity {

    private TextView date;
    private ListView listview;
    private LocalDate showedDate;
    private final DateTimeFormatter dateFormatter1 = DateTimeFormatter.ofPattern("EEEE dd/MM");
    private final DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("yyyy-dd-MM");
    private Database database;
    private final ImageView[] stars = new ImageView[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_modify_data);

        date = findViewById(R.id.date);
        ImageView left = findViewById(R.id.left);
        ImageView right = findViewById(R.id.right);
        listview = findViewById(R.id.listview);
        stars[0] = findViewById(R.id.star1);
        stars[1] = findViewById(R.id.star2);
        stars[2] = findViewById(R.id.star3);
        stars[3] = findViewById(R.id.star4);
        stars[4] = findViewById(R.id.star5);

        database = new Database(this);
        showedDate = LocalDate.now();

        date.setOnClickListener(v-> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
                showedDate = LocalDate.of(year, month+1, day);
                RefreshData();
            }, showedDate.getYear(), showedDate.getMonthValue()-1, showedDate.getDayOfMonth());
            datePickerDialog.show();
        });

        left.setOnClickListener(v-> {
            showedDate = showedDate.minusDays(1);
            RefreshData();
        });

        right.setOnClickListener(v-> {
            showedDate = showedDate.plusDays(1);
            RefreshData();
        });

        LinearLayout add = findViewById(R.id.add);
        add.setOnClickListener(v-> {
            Intent intent = new Intent(ModifyData.this, ActivityEditor.class);
            intent.putExtra("Operation", "Add");
            intent.putExtra("Date", dateFormatter2.format(showedDate));
            startActivity(intent);
        });

        for (int i=0;i<5;i++) {
            int j = i+1;
            stars[i].setOnClickListener(v-> {
                setRating(j);
                database.setRating(j, dateFormatter2.format(showedDate));
            });
            stars[i].setOnLongClickListener(v->{
                setRating(0);
                database.setRating(0, dateFormatter2.format(showedDate));
                return true;
            });
        }

    }

    private void RefreshData() {
        date.setText(dateFormatter1.format(showedDate));
        ArrayList<Activity> activities = database.getDay(dateFormatter2.format(showedDate));
        Collections.sort(activities);
        ListAdapter adapter = new ListAdapter(activities);
        listview.setAdapter(adapter);
        setListHeight(listview);
        setRating(database.getRating(dateFormatter2.format(showedDate)));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void resetRatingBar() {
        for (int i=0;i<5;i++) {
            stars[i].setImageDrawable(ModifyData.this.getResources().getDrawable(R.drawable.star));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setRating(int rating) {
        resetRatingBar();
        for (int i=0;i<rating;i++) {
            stars[i].setImageDrawable(ModifyData.this.getResources().getDrawable(R.drawable.filledstar));
        }
    }

    private void setListHeight(ListView list) {
        android.widget.ListAdapter adapter = list.getAdapter();
        if (adapter==null) {
            return;
        }
        int height = 0;
        for (int i=0;i<adapter.getCount();i++) {
            View listitem = adapter.getView(i, null, list);
            listitem.measure(0, 0);
            height+=listitem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams par = list.getLayoutParams();
        par.height = height + (list.getDividerHeight() * (adapter.getCount()-1));
        list.setLayoutParams(par);
        list.requestLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshData();
    }

    private class ListAdapter extends BaseAdapter {

        private final ArrayList<Activity> activities;

        public ListAdapter(ArrayList<Activity> activities) {
            this.activities = activities;
        }

        @Override
        public int getCount() {
            return activities.size();
        }

        @Override
        public Activity getItem(int i) {
            return activities.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            @SuppressLint({"ViewHolder", "InflateParams"})
            View v = getLayoutInflater().inflate(R.layout.activitylistitem, null);
            TextView from = v.findViewById(R.id.from);
            TextView to = v.findViewById(R.id.to);
            TextView activity = v.findViewById(R.id.activity);
            from.setText(activities.get(i).getFromToString());
            to.setText(activities.get(i).getToToString());
            activity.setText(activities.get(i).getActivity());

            v.setOnLongClickListener(v1-> {
                Intent intent = new Intent(ModifyData.this, ActivityEditor.class);
                intent.putExtra("Operation", "Update");
                intent.putExtra("Date", dateFormatter2.format(showedDate));
                intent.putExtra("ID", activities.get(i).getID());
                intent.putExtra("From", activities.get(i).getFromToString());
                intent.putExtra("To", activities.get(i).getToToString());
                intent.putExtra("Activity", activities.get(i).getActivity());
                startActivity(intent);
                return true;
            });
            return v;
        }
    }

}