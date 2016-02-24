package com.nave.omer.myapplication;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RegisterStudent extends AppCompatActivity {

    public static List<String> helpNeededFinal = new ArrayList<>();
    public static List<String> helpNeeded = new ArrayList<>();
    private CheckBox student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        student = (CheckBox) findViewById(R.id.studentCB);
    }

    public void next(View view) {
        //If user wants to teach
        if (student.isChecked()) {
            //Did choose at least 1 class
            if (helpNeeded.toArray().length != 0) {
                Intent i = new Intent(getBaseContext(), RegisterTeacher.class);
                Intent data = getIntent();
                i.putExtra("name", data.getStringExtra("name"));
                i.putExtra("email", data.getStringExtra("email"));
                i.putExtra("password", data.getStringExtra("password"));
                i.putExtra("location", data.getDoubleArrayExtra("location"));
                i.putExtra("education", data.getStringExtra("education"));
                i.putExtra("aboutMe", data.getStringExtra("aboutMe"));
                i.putExtra("isLearning", true);

                helpNeededFinal = helpNeeded;

                startActivity(i);
            } else {
                Toast.makeText(getBaseContext(), "Pick at least one", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent i = new Intent(getBaseContext(), RegisterTeacher.class);
            Intent data = getIntent();
            i.putExtra("name", data.getStringExtra("name"));
            i.putExtra("email", data.getStringExtra("email"));
            i.putExtra("password", data.getStringExtra("password"));
            i.putExtra("isLearning", false);
            i.putExtra("location", data.getDoubleArrayExtra("location"));

            startActivity(i);
        }
    }

    //Check/un-check all checkboxes
    public void wantToLearn(View view) {
        enOrDis(((CheckBox) view).isChecked());
    }

    //Back to RegisterScreen
    public void back(View view) {
        finish();
    }

    //Add/remove class to list
    public void lessonPressed(View view) {
        CheckBox check = (CheckBox) view;

        if (check.isChecked()) {
            helpNeeded.add(check.getText().toString());
        } else {
            helpNeeded.remove(check.getText().toString());
        }

        helpNeededFinal = helpNeeded;
    }

    //Check/un-check all checkboxes
    private void enOrDis(boolean enable) {
        int[] ids = new int[] {R.id.checkBox1, R.id.checkBox2, R.id.checkBox3, R.id.checkBox4, R.id.checkBox5, R.id.checkBox6, R.id.checkBox7, R.id.checkBox8, R.id.checkBox9, R.id.checkBox10, R.id.checkBox11, R.id.checkBox12, R.id.checkBox13, };

        for (int id: ids) {
            CheckBox cb = (CheckBox) findViewById(id);
            cb.setEnabled(enable);
        }

        if (!enable) {
            helpNeededFinal = new ArrayList<>();
        } else {
            helpNeededFinal = helpNeeded;
        }
    }
}
