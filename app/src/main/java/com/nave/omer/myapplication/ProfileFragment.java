package com.nave.omer.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;


public class ProfileFragment extends Fragment {

    public static List<String> helpNeeded = new ArrayList<>();
    public static List<String> canHelp = new ArrayList<>();

    ImageView image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        for (int id: learningCBId) {
            CheckBox cb = (CheckBox) view.findViewById(id);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("log", "learn click");
                    learnCBClicked(v);
                }
            });
        }

        for (int id: teachingCBId) {
            CheckBox cb = (CheckBox) view.findViewById(id);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("log", "teach click");
                    teachCBClicked(v);
                }
            });
        }

        final ProfileFragment context = this;
        image = (ImageView) view.findViewById(R.id.profile);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooser(context, "Choose app", true);
            }
        });

        setupPage(view);

        return view;
    }

    private void setupPage(View view) {
        ParseUser user = ParseUser.getCurrentUser();

        personalInfo(view, user);
        aboutMe(view, user);
        learningSubjects(view, user);
        teachingSubjects(view, user);

        TextView education = (TextView) view.findViewById(R.id.education);
        education.setText(user.getString("Education"));
    }

    private void personalInfo(View view, ParseUser user) {
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView email = (TextView) view.findViewById(R.id.email);
        TextView birthday = (TextView) view.findViewById(R.id.birthday);
        final ImageView image = (ImageView) view.findViewById(R.id.profile);

        name.setText(user.getString("Name"));
        email.setText(user.getUsername());
        //birthday.setText(user.getString("Birthday")); *NEED TO ADD!*

        ParseFile applicantResume = (ParseFile) user.get("Profile");
        applicantResume.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    Bitmap bitmap = ImageCircleCrop.uncompress(data);
                    image.setImageBitmap(bitmap);
                } else {
                    image.setImageResource(R.drawable.profile_placeholder);
                }
            }
        });
    }

    private void aboutMe(View view, ParseUser user) {
        TextView aboutMe = (TextView) view.findViewById(R.id.about_me);
        TextView rate = (TextView) view.findViewById(R.id.rate);

        aboutMe.setText(user.getString("AboutMe"));
        rate.setText(String.valueOf(user.getDouble("Rate")));
    }

    int[] learningCBId = new int[] {R.id.checkBox1, R.id.checkBox2, R.id.checkBox3, R.id.checkBox4, R.id.checkBox5, R.id.checkBox6, R.id.checkBox7, R.id.checkBox8, R.id.checkBox9, R.id.checkBox10, R.id.checkBox11, R.id.checkBox12, R.id.checkBox13};
    private void learningSubjects(View view, ParseUser user) {
        helpNeeded = (List<String>) user.get("needHelp");
        for (int id: learningCBId) {
            CheckBox cb = (CheckBox) view.findViewById(id);
            if (helpNeeded.contains(cb.getText())) {
                cb.setChecked(true);
            }
        }
    }

    int[] teachingCBId = new int[] {R.id.checkBox14, R.id.checkBox15, R.id.checkBox16, R.id.checkBox17, R.id.checkBox18, R.id.checkBox19, R.id.checkBox20, R.id.checkBox21, R.id.checkBox22, R.id.checkBox23, R.id.checkBox24, R.id.checkBox25, R.id.checkBox26};
    private void teachingSubjects(View view, ParseUser user) {
        canHelp = (List<String>) user.get("canTeach");
        for (int id: teachingCBId) {
            CheckBox cb = (CheckBox) view.findViewById(id);
            if (canHelp.contains(cb.getText())) {
                cb.setChecked(true);
            }
        }
    }

    public void learnCBClicked(View view) {
        CheckBox check = (CheckBox) view;
        ParseUser user = ParseUser.getCurrentUser();

        if (check.isChecked()) {
            helpNeeded.add(check.getText().toString());
        } else {
            helpNeeded.remove(check.getText().toString());
        }

        Log.i("log", helpNeeded.toString());

        user.remove("needHelp");
        user.addAllUnique("needHelp", helpNeeded);
        user.saveInBackground();
    }

    public void teachCBClicked(View view) {
        CheckBox check = (CheckBox) view;
        ParseUser user = ParseUser.getCurrentUser();

        if (check.isChecked()) {
            canHelp.add(check.getText().toString());
        } else {
            canHelp.remove(check.getText().toString());
        }

        user.remove("canTeach");
        user.addAllUnique("canTeach", canHelp);
        user.saveInBackground();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If cropped image returned
        if (requestCode == 1231 && resultCode == 1) {
            byte[] bytes = RegisterScreen.byteBPM;
            Bitmap bmp = ImageCircleCrop.uncompress(bytes);

            image.setImageBitmap(bmp);

            ParseUser user = ParseUser.getCurrentUser();
            ParseFile imageFile = new ParseFile(RegisterScreen.byteBPM);
            user.put("Profile", imageFile);

            //Save profile image
            imageFile.saveInBackground();

            return;
        }

        //When image picked/shot
        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            //@Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                //Handle the image
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

                Intent i = new Intent(getActivity(), ImageCircleCrop.class);
                RegisterScreen.byteBPM = RegisterScreen.scaleDownBitmap(bitmap);

                startActivityForResult(i, 1231);
            }
        });
    }
}
