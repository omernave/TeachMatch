package com.nave.omer.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Fragment fragment = new MainFragment();

        //Show teacher list
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();

        //Setup side menu
        setupMenuDrawer();
    }

    Drawer result;
    private void setupMenuDrawer() {
        final Activity act = this;
        final ParseUser user = ParseUser.getCurrentUser();
        ParseFile applicantResume = (ParseFile) user.get("Profile");
        applicantResume.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                Bitmap bitmap;
                if (e == null) {
                    bitmap = ImageCircleCrop.uncompress(data);

                } else {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_placeholder);
                }


                IProfile profile = (new ProfileDrawerItem().withName(user.getString("Name")).withEmail(user.getEmail()).withIcon(bitmap));

                // Create the AccountHeader
                final AccountHeader headerResult = new AccountHeaderBuilder()
                        .withActivity(act)
                        .withHeaderBackground(R.drawable.profile_back_image)
                        .addProfiles(
                                profile
                        )
                        .withSelectionListEnabledForSingleProfile(false)
                        .build();

                //create the drawer and remember the `Drawer` result object
                result = new DrawerBuilder()
                        .withActivity(act)
                        .withSelectedItem(-1)
                        .addDrawerItems(
                                new PrimaryDrawerItem().withName("Find Teachers"),
                                new PrimaryDrawerItem().withName("Messages"),
                                new SectionDrawerItem().withName("Meetings"),
                                new PrimaryDrawerItem().withName("Planned Lessons"),
                                new SectionDrawerItem().withName("Settings"),
                                new PrimaryDrawerItem().withName("Profile"),
                                new PrimaryDrawerItem().withName("General"),
                                new DividerDrawerItem(),
                                new PrimaryDrawerItem().withName("Logout")
                        )
                        .withAccountHeader(headerResult)
                        .build();


                result.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (position == 1) {
                            Fragment fragment = new MainFragment();

                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.replace(R.id.fragment, fragment);
                            transaction.commit();
                        }

                        if (position == 2) {
                            Fragment fragment = new MessagesFragment();

                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.replace(R.id.fragment, fragment);
                            transaction.commit();
                        }

                        if (position == 4) {
                            Fragment fragment = new MeetingsFragment();

                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.replace(R.id.fragment, fragment);
                            transaction.commit();
                        }

                        if (position == 6) {
                            Fragment fragment = new ProfileFragment();

                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.replace(R.id.fragment, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }

                        if (position == 7) {
                            Fragment fragment = new AppSettingsFragment();

                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.replace(R.id.fragment, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }

                        if (position == 9) {
                            //Remove all saved data
                            SharedPreferences preferences = getSharedPreferences("chat", 0);
                            preferences.edit().clear().commit();

                            SharedPreferences preferences2 = getSharedPreferences("settings", 0);
                            preferences.edit().clear().commit();

                            SQLiteDatabase eventsDB = openOrCreateDatabase("Events", Context.MODE_PRIVATE, null);

                            try {
                                eventsDB.execSQL("DELETE * FROM Lessons");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //Logout and go to FirstLaunch
                            ParseUser.getCurrentUser().logOut();
                            Intent i = new Intent(getBaseContext(), FirstLaunch.class);
                            startActivity(i);
                        }

                        result.closeDrawer();
                        return true;
                    }
                });
            }
        });
    }

    public void openMenu(View view) {
        result.openDrawer();
    }

    //Disable back button
    @Override
    public void onBackPressed() {
        return;
    }
}
