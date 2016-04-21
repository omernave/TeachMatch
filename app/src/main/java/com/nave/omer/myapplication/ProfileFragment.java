package com.nave.omer.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;


public class ProfileFragment extends Fragment {

    public static List<String> helpNeeded = new ArrayList<>();
    public static List<String> canHelp = new ArrayList<>();

    Map<String, String> values = new HashMap<String, String>();
    ImageView image;
    View view;
    TextView name, email, birthday, education, aboutMe, rate;

    int[] fieldsId = new int[] {R.id.name, R.id.birthday, R.id.education, R.id.about_me, R.id.rate};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView changepass = (TextView) view.findViewById(R.id.changeps);
        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword(v);
            }
        });

        for (int id: learningCBId) {
            CheckBox cb = (CheckBox) view.findViewById(id);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    learnCBClicked(v);
                }
            });
        }

        for (int id: teachingCBId) {
            CheckBox cb = (CheckBox) view.findViewById(id);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    teachCBClicked(v);
                }
            });
        }

        for (int id: fieldsId) {
            TextView cb = (TextView) view.findViewById(id);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fieldClicked(v);
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

        personalInfo(user);
        aboutMe(user);
        learningSubjects(user);
        teachingSubjects(user);

        education = (TextView) view.findViewById(R.id.education);
        education.setText(user.getString("Education"));

        values.put("Education", education.getText().toString());
    }

    private void personalInfo(ParseUser user) {
        name = (TextView) view.findViewById(R.id.name);
        email = (TextView) view.findViewById(R.id.email);
        birthday = (TextView) view.findViewById(R.id.birthday);
        final ImageView image = (ImageView) view.findViewById(R.id.profile);

        name.setText(user.getString("Name"));
        email.setText(user.getUsername());
        birthday.setText(user.getString("Birthday"));

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

        values.put("Name", name.getText().toString());
        values.put("email", email.getText().toString());
        values.put("Birthday", birthday.getText().toString());
    }

    private void aboutMe(ParseUser user) {
        aboutMe = (TextView) view.findViewById(R.id.about_me);
        rate = (TextView) view.findViewById(R.id.rate);

        aboutMe.setText(user.getString("AboutMe"));
        rate.setText(String.valueOf(user.getDouble("Rate")) + " $/Hour");

        values.put("Rate", rate.getText().toString());
        values.put("AboutMe", aboutMe.getText().toString());
    }

    public void fieldClicked(View view) {
        switch (view.getId()) {
            case R.id.name:
                openDialog("Change name:", "Name");
                break;
            case R.id.email:
                Toast.makeText(getActivity(), "You can't change your email address",
                        Toast.LENGTH_LONG).show();
            case R.id.birthday:
                openDialog("Change date:", "Birthday");
                break;
            case R.id.education:
                openDialog("Change education:", "Education");
                break;
            case R.id.about_me:
                openDialog("Change personal info:", "AboutMe");
                break;
            case R.id.rate:
                openDialog("Change rate:", "Rate");
                break;
        }
    }

    private void openDialog(String title, String key) {
        if (key == "Birthday") {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(), dplistner, 2016, 1, 1);
            datePickerDialog.show();
            return;
        }
        final String fkey = key;
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(title);

        final EditText input = new EditText(view.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        if (key == "Name") {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        }
        if (key == "AboutMe") {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finalizeChange(input.getText().toString(), fkey);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private DatePickerDialog.OnDateSetListener dplistner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ParseUser.getCurrentUser().put("Birthday", dayOfMonth + "/" + monthOfYear + "/" + year);
            ParseUser.getCurrentUser().saveInBackground();
        }
    };

    private void finalizeChange(String value, String key) {
        boolean check = true;
        boolean acheck = true;
        if (value != "") {
            if (key == "Rate") {
                acheck = true;
                try {
                    ParseUser.getCurrentUser().put(key, Double.parseDouble(value));
                } catch (Exception e) {
                    Toast.makeText(this.getContext(), "Please enter valid rate", Toast.LENGTH_SHORT).show();
                    check = false;
                }
            }
            if (key == "Name" && value.trim().length() >= 4 && value.trim().split(" ").length > 1) {
                acheck = true;
                ParseUser.getCurrentUser().put(key, value);
                ParseUser.getCurrentUser().saveInBackground();
            } else {
                if (acheck) {
                    Toast.makeText(this.getContext(), "Please enter full name", Toast.LENGTH_SHORT).show();
                    check = false;
                }
            }
            if (key == "AboutMe" && value.split(" ").length >= 20) {
                acheck = true;
                ParseUser.getCurrentUser().put(key, value);
                ParseUser.getCurrentUser().saveInBackground();
            } else {
                if (acheck) {
                    Toast.makeText(this.getContext(), "Please enter at least 20 words", Toast.LENGTH_SHORT).show();
                    check = false;
                }
            }
        } else {
            Toast.makeText(this.getContext(), "Please enter a value", Toast.LENGTH_SHORT).show();
            check = false;
        }

        if (check) {
            values.put(key, value);

            name.setText(values.get("Name"));
            email.setText(values.get("email"));
            birthday.setText(values.get("Birthday"));
            education.setText(values.get("Education"));
            aboutMe.setText(values.get("AboutMe"));
            rate.setText(values.get("Rate"));
        }
    }


    int[] learningCBId = new int[] {R.id.checkBox1, R.id.checkBox2, R.id.checkBox3, R.id.checkBox4, R.id.checkBox5, R.id.checkBox6, R.id.checkBox7, R.id.checkBox8, R.id.checkBox9, R.id.checkBox10, R.id.checkBox11, R.id.checkBox12, R.id.checkBox13};
    private void learningSubjects(ParseUser user) {
        helpNeeded = (List<String>) user.get("needHelp");
        for (int id: learningCBId) {
            CheckBox cb = (CheckBox) view.findViewById(id);
            if (helpNeeded.contains(cb.getText())) {
                cb.setChecked(true);
            }
        }
    }

    int[] teachingCBId = new int[] {R.id.checkBox14, R.id.checkBox15, R.id.checkBox16, R.id.checkBox17, R.id.checkBox18, R.id.checkBox19, R.id.checkBox20, R.id.checkBox21, R.id.checkBox22, R.id.checkBox23, R.id.checkBox24, R.id.checkBox25, R.id.checkBox26};
    private void teachingSubjects(ParseUser user) {
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

    public void changePassword(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Enter new password");

        final EditText input = new EditText(view.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().length() >= 8) {
                    ParseUser.getCurrentUser().setPassword(input.getText().toString());
                    ParseUser.getCurrentUser().saveInBackground();
                    try {
                        ParseUser.logIn(values.get("email"), input.getText().toString());
                    } catch (Exception e) {
                        ParseUser.getCurrentUser().logOut();
                        Intent i = new Intent(getContext(), FirstLaunch.class);
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(getContext(), "At least 8 characters", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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
