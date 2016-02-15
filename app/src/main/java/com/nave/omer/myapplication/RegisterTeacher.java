package com.nave.omer.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegisterTeacher extends AppCompatActivity {

    private List<String> canHelpIn = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_teacher);

        //If user didn't want to learn he must teach
        Intent i = getIntent();
        boolean check = i.getBooleanExtra("isLearning", true);

        CheckBox cb = (CheckBox) findViewById(R.id.teacherCB);
        cb.setEnabled(check);
    }

    //Back to RegisterStudent
    public void back(View view) {
        finish();
    }

    public void register(View view) {
        //Did choose at least one
        if (canHelpIn.toArray().length != 0) {
            Intent data = getIntent();

            String name = data.getStringExtra("name");
            String email = data.getStringExtra("email");
            String password = data.getStringExtra("password");
            double[] location = data.getDoubleArrayExtra("location");
            List<String> lessonsNeeded = RegisterStudent.helpNeededFinal;
            byte[] image = RegisterScreen.byteBPM;
            //Register to Parse database
            finalizeRegister(name, password, email, canHelpIn, lessonsNeeded, image, location);
        } else {
            Toast.makeText(getBaseContext(), "Pick at least one", Toast.LENGTH_SHORT).show();
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
    }

    //Add/remove class from list
    public void lessonPressed(View view) {
        CheckBox check = (CheckBox) view;

        if (check.isChecked()) {
            canHelpIn.add(check.getText().toString());
        } else {
            canHelpIn.remove(check.getText().toString());
        }
    }

    //Register user to Parse database
    private void finalizeRegister(String name, String password, String email, List<String> canTeach, List<String> needHelp, byte[] image, double[] location) {
        ParseUser user = new ParseUser();

        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email.toLowerCase());

        user.put("Name", name);
        user.addAllUnique("canTeach", canTeach);
        user.addAllUnique("needHelp", needHelp);
        user.put("Location", new ParseGeoPoint(location[0], location[1]));

        //Compress profile image
        Bitmap profile = ImageCircleCrop.uncompress(RegisterScreen.byteBPM);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inSampleSize = 2;
        Bitmap compressed = Bitmap.createScaledBitmap(profile, 96, 89, false);

        //Scale down image
        user.addAllUnique("Image", Arrays.asList(RegisterScreen.scaleDownBitmap(compressed)));

        //Signup
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Intent i = new Intent(getBaseContext(), MainScreen.class);
                    startActivity(i);
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "Register unsuccessful";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
    }
}
