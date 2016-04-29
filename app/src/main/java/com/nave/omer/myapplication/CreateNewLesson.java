package com.nave.omer.myapplication;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

        //Set TextViews and TextEdits
        date = (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);
        email = (EditText) findViewById(R.id.email);
        location = (EditText) findViewById(R.id.location);

        //Setup spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.subjects_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    //Open dialog to choose date
    public void openDateDialog(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dplistner, 2016, 0, 1);
        datePickerDialog.show();
    }

    //DateSetListener
    private DatePickerDialog.OnDateSetListener dplistner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year);
        }
    };

    //Open dialog to choose time
    public void openTimeDialog(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener, 12, 0, true);
        timePickerDialog.show();
    }

    //TimeSetDialog
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

    //Close activity
    public void back(View view) {
        finish();
    }

    //Save lesson on SQLite database
    public void saveLesson(View view) {
        //Show activity indicator
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Saving Lesson");
        mDialog.setCancelable(false);
        mDialog.show();

        //Validate
        if (date.getText().toString() == "" || time.getText().toString() == "" || email.getText().toString() == "" || location.getText().toString() == "") {
            Toast.makeText(CreateNewLesson.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            mDialog.dismiss();

            return;
        }

        //Get teacher
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", email.getText().toString());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        ParseUser user = objects.get(0);

                        //Open new database - Events
                        SQLiteDatabase DB = openOrCreateDatabase("Events", Context.MODE_PRIVATE, null);
                        try {
                            //Create table - Lessons
                            DB.execSQL("CREATE TABLE IF NOT EXISTS Lessons (subject VARCHAR, teacher VARCHAR, date VARCHAR, time VARCHAR, location VARCHAR, rate VARCHAR)");

                            //Add new lesson to table
                            DB.execSQL("INSERT INTO Lessons (subject, teacher, date, time, location, rate) VALUES ('" + spinner.getSelectedItem().toString() + "', '" + user.getString("Name") + "', '" + date.getText().toString() + "'" +
                                    ", '" + time.getText().toString() + "', '" + location.getText().toString() + "', '" + user.getDouble("Rate") + "')");

                            //Are notifications enabled?
                            SharedPreferences mPrefs = getSharedPreferences("settings", 0);
                            boolean isChecked = mPrefs.getBoolean("isEnabled", true);
                            if (isChecked) {
                                Log.i("log", "Setting notification");
                                setNotification(date.getText().toString(), time.getText().toString());
                            }
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

        DateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(String.format("%s %s", date, time)));
            cal.add(Calendar.HOUR, -1);

            //Schedule notification
            scheduleNotification(CreateNewLesson.this, cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis(), 1212);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Schedule notification
    public void scheduleNotification(Context context, long delay, int notificationId) { //delay is after how much time(in millis) from current time you want to schedule the notification
        //Set notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("TeachMatch")
                .setContentText("You have a lesson today!")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notification_icon)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        //Set pending intent
        Intent intent = new Intent(context, MainScreen.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);

        Notification notification = builder.build();

        Intent notificationIntent = new Intent(context, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //Set alarm
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
}
