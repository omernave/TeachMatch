package com.nave.omer.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.nave.omer.myapplication.model.Conversation;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Temporary
        //ParseUser.getCurrentUser().logOut();

        //1 sec counter
        new CountDownTimer(1000, 1000) {
            @Override
            public void onFinish() {
                //If user is logged in go to MainScreen
                if (ParseUser.getCurrentUser() != null) {
                    double[] location = getLocation();
                    ParseUser.getCurrentUser().put("Location", new ParseGeoPoint(location[0], location[1]));
                    ParseUser.getCurrentUser().saveInBackground();

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

    private double[] getLocation() {
        //Get user current location
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = manager.getBestProvider(new Criteria(), false);

        Location location = new Location(provider);

        //NEED TO CHECK PERMISSIONS!!!
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = manager.getLastKnownLocation(provider);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        //CHECK PERMISSIONS!!!
        double[] arr = new double[2];
        if (location != null) {
            arr = new double[] {location.getLatitude(), location.getLongitude()};
        }

        return arr;
    }
}
