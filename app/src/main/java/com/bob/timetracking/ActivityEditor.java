package com.bob.timetracking;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.time.LocalTime;

public class ActivityEditor extends AppCompatActivity {

    private String operation;
    private String date;
    private Activity act;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_editor);

        TextView from = findViewById(R.id.from);
        TextView to = findViewById(R.id.to);
        EditText activity = findViewById(R.id.activity);
        Button sumbit = findViewById(R.id.submit);
        TextView delete = findViewById(R.id.delete);

        act = new Activity();

        operation = getIntent().getStringExtra("Operation");
        date = getIntent().getStringExtra("Date");
        
        Database database = new Database(this);

        if (operation.equals("Update")) {
            delete.setVisibility(View.VISIBLE);
            act.setID(getIntent().getIntExtra("ID", 0));
            act.setFrom(getIntent().getStringExtra("From"));
            act.setTo(getIntent().getStringExtra("To"));
            act.setActivity(getIntent().getStringExtra("Activity"));
            from.setText(act.getFromToString());
            to.setText(act.getToToString());
            activity.setText(act.getActivity());

            delete.setOnClickListener(v-> {
                database.deleteActivity(act.getID(), date);
                Toast.makeText(this, "Activity deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            act.setID(database.getNextActivityID(date));
            delete.setVisibility(View.GONE);
        }

        sumbit.setOnClickListener(v-> {
            if (from.getText().toString().equals("Click to select time")) {
                Toast.makeText(this, "You must select start time", Toast.LENGTH_SHORT).show();
                return;
            }
            if (to.getText().toString().equals("Click to select time")) {
                Toast.makeText(this, "You must select end time", Toast.LENGTH_SHORT).show();
                return;
            }
            if (activity.getText().toString().equals("")) {
                activity.setError("Activity cannot be empty");
                return;
            }
            act.setActivity(activity.getText().toString());

            if (operation.equals("Update")) {
                database.updateActivity(act, date);
                Toast.makeText(this, "Activity updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                database.addActivity(act, date);
                Toast.makeText(this, "Activity added successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

        });

        from.setOnClickListener(v-> {
            int hour, min;
            if (act.getFrom() == null) {
                hour = LocalTime.now().getHour();
                min = LocalTime.now().getMinute();
            } else {
                hour = act.getFrom().getHour();
                min = act.getFrom().getMinute();
            }
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    ActivityEditor.this, (timePicker, hh, mm) -> {
                        String hours = new DecimalFormat("00").format(hh);
                        String minutes = new DecimalFormat("00").format(mm);
                        String selectedTime = hours+":"+minutes;
                        act.setFrom(selectedTime);
                        from.setText(act.getFromToString());
            }, hour, min, true);
            timePickerDialog.show();
        });

        to.setOnClickListener(v-> {
            int hour, min;
            if (act.getTo() == null) {
                hour = LocalTime.now().getHour();
                min = LocalTime.now().getMinute();
            } else {
                hour = act.getTo().getHour();
                min = act.getTo().getMinute();
            }
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    ActivityEditor.this, (timePicker, hh, mm) -> {
                String hours = new DecimalFormat("00").format(hh);
                String minutes = new DecimalFormat("00").format(mm);
                String selectedTime = hours+":"+minutes;
                act.setTo(selectedTime);
                to.setText(act.getToToString());
            }, hour, min, true);
            timePickerDialog.show();
        });

    }
}