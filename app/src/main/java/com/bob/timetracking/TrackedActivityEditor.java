package com.bob.timetracking;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TrackedActivityEditor extends AppCompatActivity {

    private TrackedActivity activity;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_tracked_editor);

        EditText title = findViewById(R.id.title);
        EditText color = findViewById(R.id.color);
        EditText tags = findViewById(R.id.tags);
        Button submit = findViewById(R.id.submit);
        TextView delete = findViewById(R.id.delete);
        View colorView = findViewById(R.id.colorView);

        activity = new TrackedActivity();
        database = new Database(this);

        color.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().contains("#") && editable.toString().length()==7) {
                    int c;
                    try {
                        c = Color.parseColor(editable.toString());
                        colorView.setBackgroundColor(c);
                    } catch (Exception e) {
                        color.setError("Invalid color code");
                    }
                } else {
                    color.setError("Invalid color code");
                }
            }
        });

        String operation = getIntent().getStringExtra("Operation");

        if (operation.equals("Update")) {
            delete.setVisibility(View.VISIBLE);
            activity.setID(getIntent().getIntExtra("ID", 0));
            activity.setTitle(getIntent().getStringExtra("Title"));
            activity.setColorCode(getIntent().getStringExtra("Color"));
            activity.setTags(getIntent().getStringArrayExtra("Tags"));

            title.setText(activity.getTitle());
            color.setText(activity.getColorCode());
            StringBuilder sb = new StringBuilder();
            for (String s : activity.getTags()) {
                sb.append(s).append("\n");
            }
            tags.setText(sb.toString());

            delete.setOnClickListener(v-> {
                database.deleteTrackedActivity(activity.getID());
                Toast.makeText(this, "Activity deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            delete.setVisibility(View.GONE);
            activity.setID(database.getNextTrackedActivityID());
        }

        submit.setOnClickListener(v-> {
            if (title.getText().toString().equals("")) {
                title.setError("Title cannot be empty");
                return;
            }
            if (color.getText().toString().equals("")) {
                color.setError("Color cannot be empty");
                return;
            }
            if (!color.getText().toString().contains("#") && color.getText().toString().length()!=7) {
                color.setError("Invalid color code");
                return;
            }
            if (tags.getText().toString().equals("")) {
                tags.setError("Tags cannot be empty");
                return;
            }

            activity.setTitle(title.getText().toString());
            activity.setColorCode(color.getText().toString());
            activity.setTags(tags.getText().toString().split("\n"));

            if (operation.equals("Update")) {
                database.updateTrackedActivity(activity);
                Toast.makeText(this, "Activity updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                database.addTrackedActivity(activity);
                Toast.makeText(this, "Activity added successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}