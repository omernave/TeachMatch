package com.nave.omer.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseUser;

public class FirstLaunch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);
    }

    public void login(View view) {
        //Get login data
        String email = ((EditText) findViewById(R.id.emailLogin)).getText().toString();
        String pass = ((EditText) findViewById(R.id.passwordLogin)).getText().toString();

        //Login with Parse database
        ParseUser.logInInBackground(email, pass, new LogInCallback() {
            @Override
            public void done(ParseUser user, com.parse.ParseException e) {
                if (user != null) {
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
