package com.nave.omer.myapplication;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateNewLesson extends AppCompatActivity {

    TextView date, time;
    EditText email, location;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_lesson);

        date = (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);
        email = (EditText) findViewById(R.id.email);
        location = (EditText) findViewById(R.id.location);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.subjects_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    public void openDateDialog(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dplistner, 2016, 0, 1);
        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener dplistner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year);
        }
    };

    public void openTimeDialog(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener, 12, 0, true);
        timePickerDialog.show();
    }

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String finalTime = "";
            if (hourOfDay >= 10) {
                finalTime += hourOfDay + ":";
            } else {
                finalTime += "0" + hourOfDay + ":";
            }

            if (minute >= 10) {
                finalTime += minute;
            } else {
                finalTime += "0" + minute;
            }

            time.setText(finalTime);
        }
    };

    public void back(View view) {
        finish();
    }

    public void saveLesson(View view) {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Saving Lesson");
        mDialog.setCancelable(false);
        mDialog.show();

        if (date.getText().toString() == "" || time.getText().toString() == "" || email.getText().toString() == "" || location.getText().toString() == "") {
            Toast.makeText(CreateNewLesson.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            mDialog.dismiss();

            return;
        }

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", email.getText().toString());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        ParseUser user = objects.get(0);

                        SQLiteDatabase DB = openOrCreateDatabase("Events", Context.MODE_PRIVATE, null);
                        try {

                            DB.execSQL("CREATE TABLE IF NOT EXISTS Lessons (subject VARCHAR, teacher VARCHAR, date VARCHAR, time VARCHAR, location VARCHAR, rate VARCHAR)");

                            DB.execSQL("INSERT INTO Lessons (subject, teacher, date, time, location, rate) VALUES ('" + spinner.getSelectedItem().toString() + "', '" + user.getString("Name") + "', '" + date.getText().toString() + "'" +
                                    ", '" + time.getText().toString() + "', '" + location.getText().toString() + "', '" + user.getDouble("Rate") + "')");

                            setNotification(date.getText().toString(), time.getText().toString());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        finish();
                    } else {
                        Toast.makeText(CreateNewLesson.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateNewLesson.this, "Network error", Toast.LENGTH_SHORT).show();
                }
                mDialog.dismiss();

            }
        });
    }

    private void setNotification(String date, String time) {
        //SET LOCAL NOTIFICATION
    }
}
