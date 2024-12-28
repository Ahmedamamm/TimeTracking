package com.bob.timetracking;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    public Database(Context context) {
        super(context, "Database", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {}

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    public void checkTable(String date) {
        String create = "CREATE TABLE IF NOT EXISTS `"+date+"` " +
                "(`ID` integer, `From` text, `To` text, `Activity` text);";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(create);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addActivity(Activity act, String date) {
        checkTable(date);
        SQLiteDatabase db = this.getWritableDatabase();
        String insert = "INSERT INTO `"+date+"` (`ID`, `From`, `To`, `Activity`) VALUES " +
                "('"+act.getID()+"', '"+act.getFromToString()+"', '"+
                act.getToToString()+"', '"+act.getActivity()+"');";
        db.execSQL(insert);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Activity> getDay(String date) {
        checkTable(date);
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Activity> day = new ArrayList<>();
        String select = "SELECT * FROM `"+date+"`;";
        Cursor cursor = db.rawQuery(select, null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                Activity activity = new Activity();
                activity.setID(cursor.getInt(0));
                activity.setFrom(cursor.getString(1));
                activity.setTo(cursor.getString(2));
                activity.setActivity(cursor.getString(3));
                day.add(activity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return day;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getNextActivityID(String date) {
        ArrayList<Activity> activities = getDay(date);
        int ID = 0;
        int size = activities.size();
        if (size!=0) {
            ID = activities.get(size-1).getID()+1;
        }
        return ID;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateActivity(Activity activity, String date) {
        String update = "UPDATE `"+date+"` SET `From` = '"+activity.getFromToString()+
                "', `TO` = '"+activity.getToToString()+"', `Activity` = '"+activity.getActivity()+
                "' WHERE `ID` = "+activity.getID()+" ;";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(update);
    }

    public void deleteActivity(int ID, String date) {
        String delete = "DELETE FROM `"+date+"` WHERE `ID` = '"+ID+"';";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(delete);
    }

    public void checkRatingTable() {
        String create = "CREATE TABLE IF NOT EXISTS `Ratings` (`Date` text, `Rating` integer);";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(create);
    }

    public String[][] getAllRatings() {
        checkRatingTable();
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Integer> ratings = new ArrayList<>();
        String select = "SELECT * FROM `Ratings`;";
        Cursor cursor = db.rawQuery(select, null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                dates.add(cursor.getString(0));
                ratings.add(cursor.getInt(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        String[][] data = new String[dates.size()][2];
        for (int i=0;i<dates.size();i++) {
            data[i][0] = dates.get(i);
            data[i][1] = String.valueOf(ratings.get(i));
        }
        return data;
    }

    public void setRating(int rating, String date) {
        checkRatingTable();
        boolean exists = false;
        for (String[] row : getAllRatings()) {
            if (row[0].equals(date)) {
                exists = true;
                break;
            }
        }

        SQLiteDatabase db = this.getWritableDatabase();
        String statement;
        if (exists) {
            statement = "UPDATE `Ratings` SET `Rating` = '"+rating+"' WHERE `Date` = '"+date+"';";
        } else {
            statement = "INSERT INTO `Ratings` (`Date`, `Rating`) VALUES ('"+date+"', '"+rating+"');";
        }
        db.execSQL(statement);
    }

    public int getRating(String date) {
        checkRatingTable();
        SQLiteDatabase db = this.getReadableDatabase();
        String statement = "SELECT * FROM `Ratings` WHERE `Date` = '"+date+"';";
        Cursor cursor = db.rawQuery(statement, null);
        cursor.moveToFirst();
        int rating = 0;
        if (cursor.moveToFirst()) {
            rating = cursor.getInt(1);
        }
        cursor.close();
        return rating;
    }

    public void checkTrackedActivitiesTable() {
        String create = "CREATE TABLE IF NOT EXISTS `Tracked Activities` " +
                "(`ID` int, `Title` text, `Color` text, `Tags` text);";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(create);
    }

    public void addTrackedActivity(TrackedActivity act) {
        checkTrackedActivitiesTable();
        SQLiteDatabase db = this.getWritableDatabase();
        String insert = "INSERT INTO `Tracked Activities` " +
                "(`ID`, `Title`, `Color`, `Tags`) VALUES " +
                "('"+act.getID()+"', '"+act.getTitle()+"', '"+act.getColorCode()+"', '"
                +act.getTagsToString()+"');";
        db.execSQL(insert);
    }

    public void updateTrackedActivity(TrackedActivity a) {
        checkTrackedActivitiesTable();
        String update = "UPDATE `Tracked Activities` SET " +
                "`Title` = '"+a.getTitle()+"', `Color` = '"+a.getColorCode()+"', " +
                "`Tags` = '"+a.getTagsToString()+"' WHERE `ID` = "+a.getID()+";";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(update);
    }

    public void deleteTrackedActivity(int ID) {
        checkTrackedActivitiesTable();
        String delete = "DELETE FROM `Tracked Activities` WHERE `ID` = '"+ID+"';";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(delete);
    }

    public ArrayList<TrackedActivity> getTrackedActivities() {
        checkTrackedActivitiesTable();
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<TrackedActivity> activities = new ArrayList<>();
        String select = "SELECT * FROM `Tracked Activities`;";
        Cursor cursor = db.rawQuery(select, null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                TrackedActivity a = new TrackedActivity();
                a.setID(cursor.getInt(0));
                a.setTitle(cursor.getString(1));
                a.setColorCode(cursor.getString(2));
                a.setTags(cursor.getString(3).split("%%%%"));
                activities.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return activities;
    }

    public int getNextTrackedActivityID() {
        ArrayList<TrackedActivity> activities = getTrackedActivities();
        int ID = 0;
        int size = activities.size();
        if (size!=0) {
            ID = activities.get(size-1).getID()+1;
        }
        return ID;
    }

}
