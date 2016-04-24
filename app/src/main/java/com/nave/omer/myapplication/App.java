package com.nave.omer.myapplication;

import android.app.Application;

import com.parse.Parse;

public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        //Initialize parse database

        //Parse.enableLocalDatastore(this);
        //Parse.initialize(this);

        Parse.initialize(new Parse.Configuration.Builder(this)
                        .applicationId("soGhecMgERCcHZ5by5lcW02UwimTLGTjHWiyOcl0")
                        .clientKey("vicKOMZ50gPti7Nj6CxryMIpWVqkvIDNaSgnl2yY")
                        .build()
        );
    }
}