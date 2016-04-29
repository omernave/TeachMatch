package com.nave.omer.myapplication;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class AppSettingsFragment extends Fragment {

    View view;
    TextView radLabel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_app_settings, container, false);

        radLabel = (TextView) view.findViewById(R.id.radlab);
        final Switch noti = (Switch) view.findViewById(R.id.switch1);

        //Get saved values for search radius and notifications
        SharedPreferences mPrefs = getContext().getSharedPreferences("settings", 0);
        boolean isChecked = mPrefs.getBoolean("isEnabled", true);
        int radius = mPrefs.getInt("radius", 10);

        noti.setChecked(isChecked);
        radLabel.setText(radius + "km");

        //Setup SeekBar
        DiscreteSeekBar seekBar = (DiscreteSeekBar) view.findViewById(R.id.seekBar);
        seekBar.setProgress(radius);
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                //Set label text
                radLabel.setText(value + "km");
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                //Save new value for radius
                int radius = seekBar.getProgress();

                SharedPreferences mPrefs = getContext().getSharedPreferences("settings", 0);
                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putInt("radius", radius).commit();
            }
        });


        noti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Save value for notification switch
                SharedPreferences mPrefs = getContext().getSharedPreferences("settings", 0);
                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putBoolean("isEnabled", isChecked).commit();

                //If disabled cancel all notifications
                if (!isChecked) {
                    AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

                    while (alarmManager.getNextAlarmClock() != null) {
                        alarmManager.cancel(alarmManager.getNextAlarmClock().getShowIntent());
                    }
                    /*
                    NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    StatusBarNotification[] notifications = notificationManager.getActiveNotifications();

                    for (StatusBarNotification notification : notifications) {
                        notificationManager.cancel(notification.getId());
                    }*/
                }
            }
        });

        return view;
    }
}
