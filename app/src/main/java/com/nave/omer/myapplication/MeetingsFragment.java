package com.nave.omer.myapplication;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MeetingsFragment extends Fragment {

    ListView listView;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_future_meetings, container, false);

        //Get list
        listView = (ListView) view.findViewById(R.id.listView);

        //Set adapter
        LessonsAdapter adp = new LessonsAdapter(getContext(), getLessons());

        //Setup new lesson button
        Button btn = (Button) view.findViewById(R.id.new_lesson);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CreateNewLesson.class);
                startActivity(i);
            }
        });

        //Set list adapter
        listView.setAdapter(adp);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Refresh list
        listView = (ListView) view.findViewById(R.id.listView);

        LessonsAdapter adp = new LessonsAdapter(getContext(), getLessons());

        listView.setAdapter(adp);
    }

    //Get all lessons
    public String[][] getLessons() {
        List<String[]> data = new ArrayList<String[]>();

        //Get/Create SQLite database - Events
        SQLiteDatabase eventsDB = getContext().openOrCreateDatabase("Events", Context.MODE_PRIVATE, null);

        try {
            //Get all lessons
            Cursor c = eventsDB.rawQuery("SELECT * FROM Lessons", null);

            //Get column indexes
            int subjectIndex = c.getColumnIndex("subject");
            int teacherIndex = c.getColumnIndex("teacher");
            int dateIndex = c.getColumnIndex("date");
            int timeIndex = c.getColumnIndex("time");
            int locationIndex = c.getColumnIndex("location");
            int rateIndex = c.getColumnIndex("rate");


            c.moveToFirst();

            while (c != null) {
                //Get lesson and add to list
                String[] lesson = new String[]{c.getString(subjectIndex), c.getString(teacherIndex), c.getString(dateIndex), c.getString(timeIndex), c.getString(locationIndex), c.getString(rateIndex)};
                data.add(lesson);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //return all lessons array
        return data.toArray(new String[data.size()][6]);
    }
}
