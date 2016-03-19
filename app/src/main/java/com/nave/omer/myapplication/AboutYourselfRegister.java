package com.nave.omer.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AboutYourselfRegister extends AppCompatActivity {

    TextView edu, about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_yourself_register);

        edu = (TextView) findViewById(R.id.education);
        about = (TextView) findViewById(R.id.aboutme);
    }

    public void next(View view) {
        String school = edu.getText().toString();
        String[] aboutArr = about.getText().toString().split(" ");

        boolean isReady = true;

        //Validate
        if (school == "") {
            edu.setError("Please fill this field");
            isReady = false;
        }
        if (aboutArr.length < 20) {
            about.setError("At least 20 words");
            isReady = false;
        }

        //Go to student register
        if (isReady) {
            Intent i = new Intent(getBaseContext(), RegisterStudent.class);
            Intent data = getIntent();
            i.putExtra("education", school);
            i.putExtra("aboutMe", (String) about.getText().toString());
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
