package com.nave.omer.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

public class AboutYourselfRegister extends AppCompatActivity {

    TextView edu, about, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_yourself_register);

        //Get TextViews
        edu = (TextView) findViewById(R.id.education);
        about = (TextView) findViewById(R.id.aboutme);
        date = (TextView) findViewById(R.id.date);
    }

    //Open dialog to choose date
    public void openDialog(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dplistner, 2016, 1, 1);
        datePickerDialog.show();
    }

    //DateSetListener
    private DatePickerDialog.OnDateSetListener dplistner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
        }
    };

    //Go to RegisterStudent
    public void next(View view) {
        String school = edu.getText().toString();
        String[] aboutArr = about.getText().toString().split(" ");

        boolean isReady = true;

        //Validate form
        if (school == "") {
            edu.setError("Please fill this field");
            isReady = false;
        }
        if (aboutArr.length < 20) {
            about.setError("At least 20 words");
            isReady = false;
        }
        if (date.getText().toString() == "") {
            about.setError("Enter birthday date");
            isReady = false;
        }

        //Save data + Go to student register
        if (isReady) {
            Intent i = new Intent(getBaseContext(), RegisterStudent.class);
            Intent data = getIntent();
            i.putExtra("education", school);
            i.putExtra("aboutMe", (String) about.getText().toString());
            i.putExtra("birthday", date.getText().toString());
            i.putExtra("name", data.getStringExtra("name"));
            i.putExtra("email", data.getStringExtra("email"));
            i.putExtra("password", data.getStringExtra("password"));
            i.putExtra("location", data.getDoubleArrayExtra("location"));

            startActivity(i);
        }
    }

    //Go back
    public void back(View view) {
        finish();
    }
}
