package com.nave.omer.myapplication;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        SharedPreferences mPrefs = getContext().getSharedPreferences("settings", 0);
        int radius = mPrefs.getInt("radius", 10);

        radLabel.setText(radius + "km");

        DiscreteSeekBar seekBar = (DiscreteSeekBar) view.findViewById(R.id.seekBar);
        seekBar.setProgress(radius);
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                radLabel.setText(value + "km");
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                int radius = seekBar.getProgress();

                SharedPreferences mPrefs = getContext().getSharedPreferences("settings", 0);
                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putInt("radius", radius).commit();
            }
        });

        return view;
    }
}
