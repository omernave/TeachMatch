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

        listView = (ListView) view.findViewById(R.id.listView);

        LessonsAdapter adp = new LessonsAdapter(getContext(), getLessons());

        Button btn = (Button) view.findViewById(R.id.new_lesson);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CreateNewLesson.class);
                startActivity(i);
            }
        });

        listView.setAdapter(adp);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        listView = (ListView) view.findViewById(R.id.listView);

        LessonsAdapter adp = new LessonsAdapter(getContext(), getLessons());

        listView.setAdapter(adp);
    }

    public String[][] getLessons() {
        List<String[]> data = new ArrayList<String[]>();

        SQLiteDatabase eventsDB = getContext().openOrCreateDatabase("Events", Context.MODE_PRIVATE, null);

        try {
            Cursor c = eventsDB.rawQuery("SELECT * FROM Lessons", null);

            int subjectIndex = c.getColumnIndex("subject");
            int teacherIndex = c.getColumnIndex("teacher");
            int dateIndex = c.getColumnIndex("date");
            int timeIndex = c.getColumnIndex("time");
            int locationIndex = c.getColumnIndex("location");
            int rateIndex = c.getColumnIndex("rate");


            c.moveToFirst();

            while (c != null) {
                String[] lesson = new String[]{c.getString(subjectIndex), c.getString(teacherIndex), c.getString(dateIndex), c.getString(timeIndex), c.getString(locationIndex), c.getString(rateIndex)};
                data.add(lesson);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return data.toArray(new String[data.size()][6]);
    }
}
