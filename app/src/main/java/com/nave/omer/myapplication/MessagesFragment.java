package com.nave.omer.myapplication;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
    ProgressDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_messages, container, false);

        //Show activity indicator
        mDialog = new ProgressDialog(getContext());
        mDialog.setMessage("Loading messages...");
        mDialog.setCancelable(false);
        mDialog.show();

        //get ListView
        list = (ListView) view.findViewById(R.id.conversations);

        //Set OnItemClickListener
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Go to Chat and pass email as extra
                ParseUser item = (ParseUser) list.getItemAtPosition(position);
                String email = (String) item.getEmail();

                Intent i = new Intent(getContext(), Chat.class);
                i.putExtra("email", email);

                getContext().startActivity(i);
            }
        });

        //Check for received messages
        checkReceivedMessages();

        return view;
    }

    //Check if user received message from new user and save email
    List<String> recList = new ArrayList<String>();
    private void checkReceivedMessages() {
        //Get email list of users that current user has a chat with
        final SharedPreferences mPrefs = getContext().getSharedPreferences("chat", 0);
        Set<String> recipients = mPrefs.getStringSet("MessagedTo", null);
        if (recipients != null) {
            recList = new ArrayList<String>(recipients);
        }

        //Query to get users current user chats with
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Chat");

        ArrayList<String> al = new ArrayList<String>();
        al.add(ParseUser.getCurrentUser().getEmail().toString());
        q.whereContainedIn("receiver", al);
        q.orderByDescending("createdAt");
        q.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> li, com.parse.ParseException e) {
                if (e == null) {
                    //add the users that aren't saved in SharedPreferences
                    for (ParseObject obj: li) {
                        if (!recList.contains(obj.get("sender"))) {
                            recList.add(obj.get("sender").toString());
                        }
                    }

                    SharedPreferences.Editor mEditor = mPrefs.edit();
                    mEditor.putStringSet("MessagedTo", new HashSet<String>(recList)).commit();

                    getRecipients();
                } else {
                    mDialog.dismiss();
                }
            }
        });
    }

    //Get users by email
    public void getRecipients() {
        //Get saved email list
        final SharedPreferences mPrefs = this.getContext().getSharedPreferences("chat", 0);
        Set<String> recipients = mPrefs.getStringSet("MessagedTo", null);
        List<String> recList = new ArrayList<String>();

        if (recipients != null) {
            recList = new ArrayList<String>(recipients);
        }

        //Get user with matching emails
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("email", recList);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                if (e == null) {
                    //Setup ListView with users list
                    setupList(objects);
                } else {
                    mDialog.dismiss();
                }
            }
        });
    }

    public void setupList(List<ParseUser> list) {
        //List to array
        ParseUser[] arr = new ParseUser[list.size()];

        int index = 0;
        for (ParseUser teacher : list) {
            arr[index] = teacher;
            index++;
        }

        //Set adapter
        MessagesListAdapter adapter = new MessagesListAdapter(getContext(), arr);
        this.list.setAdapter(adapter);

        mDialog.dismiss();
    }
}
