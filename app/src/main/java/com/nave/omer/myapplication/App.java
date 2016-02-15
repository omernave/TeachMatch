package com.nave.omer.myapplication;

import android.app.Application;

import com.parse.Parse;

public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        //Initialize parse database
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}