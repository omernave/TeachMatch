package com.nave.omer.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FirstLaunch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);
    }

    public void login(View view) {
        final ProgressDialog mDialog = new ProgressDialog(FirstLaunch.this);
        mDialog.setMessage("Logging in...");
        mDialog.setCancelable(false);
        mDialog.show();
        //Get login data
        String email = ((EditText) findViewById(R.id.emailLogin)).getText().toString();
        String pass = ((EditText) findViewById(R.id.passwordLogin)).getText().toString();

        //Login with Parse database
        ParseUser.logInInBackground(email, pass, new LogInCallback() {
            @Override
            public void done(ParseUser user, com.parse.ParseException e) {
                mDialog.dismiss();
                if (user != null) {
                    checkSentMessages();
                    Intent i = new Intent(getBaseContext(), MainScreen.class);
                    startActivity(i);
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "Login unsuccessful";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
    }

    List<String> recList = new ArrayList<String>();
    private void checkSentMessages() {
        final SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("chat", 0);
        Set<String> recipients = mPrefs.getStringSet("MessagedTo", null);
        if (recipients != null) {
            recList = new ArrayList<String>(recipients);
        }

        ParseQuery<ParseObject> q = ParseQuery.getQuery("Chat");

        ArrayList<String> al = new ArrayList<String>();
        al.add(ParseUser.getCurrentUser().getEmail().toString());
        q.whereContainedIn("sender", al);
        q.orderByDescending("createdAt");
        q.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> li, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject obj : li) {
                        if (!recList.contains(obj.get("receiver"))) {
                            recList.add(obj.get("receiver").toString());
                        }
                    }

                    SharedPreferences.Editor mEditor = mPrefs.edit();
                    mEditor.putStringSet("MessagedTo", new HashSet<String>(recList)).commit();
                }
            }
        });
    }

    //Go to RegisterScreen
    public void register(View view) {
        Intent i = new Intent(getBaseContext(), RegisterScreen.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
