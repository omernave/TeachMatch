package com.nave.omer.myapplication;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessagesFragment extends Fragment {

    View view;
    ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_messages, container, false);

        list = (ListView) view.findViewById(R.id.conversations);

        checkReceivedMessages();

        return view;
    }

    List<String> recList = new ArrayList<String>();
    private void checkReceivedMessages() {
        final SharedPreferences mPrefs = getContext().getSharedPreferences("chat", 0);
        Set<String> recipients = mPrefs.getStringSet("MessagedTo", null);
        if (recipients != null) {
            recList = new ArrayList<String>(recipients);
        }

        ParseQuery<ParseObject> q = ParseQuery.getQuery("Chat");

        ArrayList<String> al = new ArrayList<String>();
        al.add(ParseUser.getCurrentUser().getEmail().toString());
        q.whereContainedIn("receiver", al);
        q.orderByDescending("createdAt");
        q.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> li, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject obj: li) {
                        if (!recList.contains(obj.get("sender"))) {
                            recList.add(obj.get("sender").toString());
                        }
                    }

                    SharedPreferences.Editor mEditor = mPrefs.edit();
                    mEditor.putStringSet("MessagedTo", new HashSet<String>(recList)).commit();

                    getRecipients();
                }
            }
        });
    }

    public void getRecipients() {
        SharedPreferences mPrefs = this.getContext().getSharedPreferences("chat", 0);
        Set<String> recipients = mPrefs.getStringSet("MessagedTo", null);
        List<String> recList = new ArrayList<String>();

        if (recipients != null) {
            recList = new ArrayList<String>(recipients);
        }

        Log.i("log", "reList size - " + recList.size());

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("email", recList);
        //query.whereEqualTo("email", recList);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                if (e == null) {
                    setupList(objects);
                }
            }
        });
    }

    public void setupList(List<ParseUser> list) {
        ParseUser[] arr = new ParseUser[list.size()];

        int index = 0;
        for (ParseUser teacher : list) {
            arr[index] = teacher;
            index++;
        }

        //Set adapter
        MessagesListAdapter adapter = new MessagesListAdapter(getContext(), arr);
        this.list.setAdapter(adapter);
    }
}
