package com.nave.omer.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.parse.ParseUser;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Temporary
        ParseUser.getCurrentUser().logOut();

        //1 sec counter
        new CountDownTimer(1000, 1000) {
            @Override
            public void onFinish() {
                //If user is logged in go to MainScreen
                if (ParseUser.getCurrentUser() != null) {
                    Intent i = new Intent(getBaseContext(), MainScreen.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getBaseContext(), FirstLaunch.class);
                    startActivity(i);
                }
            }

            @Override
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }

}
