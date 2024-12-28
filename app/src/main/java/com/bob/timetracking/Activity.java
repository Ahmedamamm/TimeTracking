package com.bob.timetracking;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Activity implements Comparable<Activity> {

    private int ID;
    private LocalTime from, to;
    private String activity;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Activity(int ID, String from, String to, String activity) {
        this.ID = ID;
        this.from = LocalTime.parse(from, formatter);
        this.to = LocalTime.parse(to, formatter);
        this.activity = activity;
    }

    public Activity() {}

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public LocalTime getFrom() {
        return from;
    }

    public String getFromToString() {
        return from.format(formatter);
    }

    public void setFrom(String from) {
        this.from = LocalTime.parse(from, formatter);
    }

    public LocalTime getTo() {
        return to;
    }

    public String getToToString() {
        return to.format(formatter);
    }

    public void setTo(String to) {
        this.to = LocalTime.parse(to, formatter);
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public long getTime() {
        return ChronoUnit.MINUTES.between(from, to);
    }

    @Override
    public int compareTo(Activity activity) {
        return from.compareTo(activity.getFrom());
    }
}
