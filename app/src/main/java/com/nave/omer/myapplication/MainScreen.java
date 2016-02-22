package com.nave.omer.myapplication;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AppCompatActivity {

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        list = (ListView) findViewById(R.id.teacherList);

        getAvalibleTeacherList();
    }

    //Get relevant teachers
    byte[] image;
    private void getAvalibleTeacherList() {
        int SEARCHRADIUS = 10;

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereWithinKilometers("Location", (ParseGeoPoint) ParseUser.getCurrentUser().get("Location"), SEARCHRADIUS);
        query.whereContainedIn("canTeach", (ArrayList<String>) ParseUser.getCurrentUser().get("needHelp"));
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    setupList(objects);
                } else {
                    Log.i("log", "ERROR");
                }
            }
        });
    }

    //Setup teacher list view
    private void setupList(List<ParseUser> teacherObjectList) {
        ParseUser[] teacherObjects = new ParseUser[teacherObjectList.size()];

        //Convert list to array
        int index = 0;
        for (ParseUser teacher : teacherObjectList) {
            teacherObjects[index] = teacher;
            index++;
        }

        //Set adapter
        TeacherListAdapter adapter = new TeacherListAdapter(this, teacherObjects);
        list.setAdapter(adapter);
    }
}
