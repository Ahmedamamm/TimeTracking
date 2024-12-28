package com.bob.timetracking;

public class TrackedActivity {

    private int ID;
    private String title;
    private String colorCode;
    private String[] tags;
    private double time;

    public TrackedActivity(int ID, String title, String colorCode, String[] tags, double time) {
        this.ID = ID;
        this.title = title;
        this.colorCode = colorCode;
        this.tags = tags;
        this.time = time;
    }

    public TrackedActivity() {}

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String[] getTags() {
        return tags;
    }

    public String getTagsToString() {
        StringBuilder sb = new StringBuilder();
        for (String s : tags) {
            sb.append(s).append("%%%%");
        }
        return sb.toString();
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

}
