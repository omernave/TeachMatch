package com.nave.omer.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        // Inflate the layout for this fragment
        list = (ListView) view.findViewById(R.id.teacherList);

        getAvalibleTeacherList();

        return view;
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
        TeacherListAdapter adapter = new TeacherListAdapter(getContext(), teacherObjects);
        list.setAdapter(adapter);
    }
}
