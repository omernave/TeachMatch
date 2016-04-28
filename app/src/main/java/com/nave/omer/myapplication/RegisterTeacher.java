package com.nave.omer.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegisterTeacher extends AppCompatActivity {

    private List<String> canHelpIn = new ArrayList<>();
    List<String> canHelpInFinal = new ArrayList<>();
    CheckBox isTeaching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_teacher);

        //If user didn't want to learn he must teach
        Intent i = getIntent();
        boolean check = i.getBooleanExtra("isLearning", true);

        isTeaching = (CheckBox) findViewById(R.id.teacherCB);
        isTeaching.setEnabled(check);
    }

    //Back to RegisterStudent
    public void back(View view) {
        finish();
    }

    public void register(View view) {
        double rate = 0.0;
        EditText rateET = (EditText) findViewById(R.id.rateTextView);
        try {
            if (rateET.isEnabled()) {
                rate = Double.parseDouble(rateET.getText().toString());
            }

            Intent data = getIntent();
            String education = data.getStringExtra("education");
            String aboutMe = data.getStringExtra("aboutMe");
            String date = data.getStringExtra("birthday");
            String name = data.getStringExtra("name");
            String email = data.getStringExtra("email");
            String password = data.getStringExtra("password");
            double[] location = data.getDoubleArrayExtra("location");
            List<String> lessonsNeeded = RegisterStudent.helpNeededFinal;
            byte[] image = RegisterScreen.byteBPM;

            //Did choose at least one
            if (isTeaching.isChecked()) {
                if (canHelpInFinal.toArray().length != 0) {
                    //Register to Parse database
                    finalizeRegister(name, password, email, canHelpInFinal, lessonsNeeded, rate, image, location, education, aboutMe, date);
                } else {
                    Toast.makeText(getBaseContext(), "Pick at least one", Toast.LENGTH_SHORT).show();
                }
            } else {
                finalizeRegister(name, password, email, canHelpInFinal, lessonsNeeded, rate, image, location, education, aboutMe, date);
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Please input valid rate", Toast.LENGTH_SHORT).show();
        }
    }

    //Check/uncheck all checkboxs
    public void wantToTeach(View view) {
        enOrDis(((CheckBox) view).isChecked());
    }

    //Check/uncheck all checkboxs
    private void enOrDis(boolean enable) {
        int[] ids = new int[] {R.id.checkBox1, R.id.checkBox2, R.id.checkBox3, R.id.checkBox4, R.id.checkBox5, R.id.checkBox6, R.id.checkBox7, R.id.checkBox8, R.id.checkBox9, R.id.checkBox10, R.id.checkBox11, R.id.checkBox12, R.id.checkBox13, };

        for (int id: ids) {
            CheckBox cb = (CheckBox) findViewById(id);
            cb.setEnabled(enable);
        }

        EditText rateET = (EditText) findViewById(R.id.rateTextView);
        rateET.setEnabled(enable);

        if (!enable) {
            canHelpInFinal = new ArrayList<>();
        } else {
            canHelpInFinal = canHelpIn;
        }
    }

    //Add/remove class from list
    public void lessonPressed(View view) {
        CheckBox check = (CheckBox) view;

        if (check.isChecked()) {
            canHelpIn.add(check.getText().toString());
        } else {
            canHelpIn.remove(check.getText().toString());
        }

        canHelpInFinal = canHelpIn;
    }

    //Register user to Parse database
    ParseUser user = new ParseUser();
    private void finalizeRegister(String name, String password, String email, List<String> canTeach, List<String> needHelp, double rate, byte[] image, double[] location, String education, String about, String date) {
        final ProgressDialog mDialog = new ProgressDialog(RegisterTeacher.this);
        mDialog.setMessage("Registering...");
        mDialog.setCancelable(false);
        mDialog.show();

        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email.toLowerCase());

        user.put("Name", name);
        user.put("Rate", rate);

        //Set new rating object
        ParseObject ratings = new ParseObject("Ratings");
        ratings.put("email", email);
        ratings.put("Sum", 0);
        ratings.put("numOfVoters", 0);
        ratings.saveInBackground();

        user.put("Location", new ParseGeoPoint(location[0], location[1]));
        user.put("AboutMe", about);
        user.put("Education", education);
        user.put("Birthday", date);
        user.addAllUnique("canTeach", canTeach);
        user.addAllUnique("needHelp", needHelp);


        ParseFile imageFile = new ParseFile(RegisterScreen.byteBPM);
        user.put("Profile", imageFile);

        //Save profile image
        imageFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    //Signup user
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            mDialog.dismiss();
                            //If successful go to main screen
                            if (e == null) {
                                Intent i = new Intent(getBaseContext(), MainScreen.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(getBaseContext(), "Register unsuccessful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    mDialog.dismiss();
                    Toast.makeText(getBaseContext(), "Register unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
