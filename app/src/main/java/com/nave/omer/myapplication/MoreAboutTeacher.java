package com.nave.omer.myapplication;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import io.techery.properratingbar.ProperRatingBar;
import io.techery.properratingbar.RatingListener;

public class MoreAboutTeacher extends AppCompatActivity {

    TextView name, email, bday, aboutMe, studies, teaches, rate;
    ProperRatingBar ratingBar, yourRating;
    ImageView profile;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_about_teacher);

        mDialog = new ProgressDialog(getApplicationContext());
        mDialog.setMessage("Loading profile...");
        mDialog.setCancelable(false);
        mDialog.show();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", getIntent().getStringExtra("email"));
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    setupPage(objects.get(0));
                } else {
                    Toast.makeText(getApplicationContext(), "Check internet connection", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    public void setupPage(ParseUser puser) {
        final ParseUser user = puser;
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        bday = (TextView) findViewById(R.id.bday);
        aboutMe = (TextView) findViewById(R.id.about_me);
        studies = (TextView) findViewById(R.id.studies);
        teaches = (TextView) findViewById(R.id.teaches);
        rate = (TextView) findViewById(R.id.rate);
        ratingBar = (ProperRatingBar) findViewById(R.id.ratingBar);
        yourRating = (ProperRatingBar) findViewById(R.id.your_rating);
        profile = (ImageView) findViewById(R.id.profile);

        name.setText(user.get("Name").toString());
        email.setText(user.getEmail());
        bday.setText(user.get("Birthday").toString());
        aboutMe.setText(user.get("AboutMe").toString());

        String teachStr = ((List<String>) user.get("canTeach")).toString();
        teaches.setText(teachStr.substring(1, teachStr.length() - 1));

        String learnStr = ((List<String>) user.get("needHelp")).toString();
        studies.setText(learnStr.substring(1, learnStr.length() - 1));

        rate.setText("$" + user.get("Rate").toString() + "/Hour");

        ParseQuery<ParseObject> q = new ParseQuery<ParseObject>("Ratings");
        q.whereEqualTo("email", user.getEmail());
        q.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    ParseObject obj = objects.get(0);

                    int num = obj.getInt("numOfVoters");
                    int sum = obj.getInt("Sum");

                    int rating = 0;
                    if (num != 0) {
                        rating = sum / num;
                    }

                    ratingBar.setRating(rating);

                }
            }
        });

        yourRating.setListener(new RatingListener() {
            @Override
            public void onRatePicked(final ProperRatingBar properRatingBar) {
                yourRating.setRating(properRatingBar.getRating());

                ParseQuery<ParseObject> q = new ParseQuery<ParseObject>("Ratings");
                q.whereEqualTo("email", user.getEmail());
                q.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            ParseObject obj = objects.get(0);

                            int num = obj.getInt("numOfVoters");
                            int sum = obj.getInt("Sum");

                            obj.put("numOfVoters", num + 1);
                            obj.put("Sum", sum + properRatingBar.getRating());
                            obj.saveInBackground();

                            Toast.makeText(getApplicationContext(), "Thanks for your vote!" + properRatingBar.getRating(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        ParseFile applicantResume = (ParseFile) user.get("Profile");
        applicantResume.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    Bitmap bitmap = ImageCircleCrop.uncompress(data);
                    profile.setImageBitmap(bitmap);
                } else {
                    profile.setImageResource(R.drawable.profile_placeholder);
                }
            }
        });

        mDialog.dismiss();
    }

    public void back(View view) {
        finish();
    }
}
