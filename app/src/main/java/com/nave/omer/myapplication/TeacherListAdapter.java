package com.nave.omer.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import io.techery.properratingbar.ProperRatingBar;

/**
 * Created by omer on 14/02/16.
 */
class TeacherListAdapter extends ArrayAdapter<ParseUser> {

    String email = "";

    public TeacherListAdapter(Context context, ParseUser[] values) {
        super(context, R.layout.teacher_card, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get cell view
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View cellView = inflater.inflate(R.layout.teacher_card, parent, false);

        final ParseUser teacher = getItem(position);

        email = teacher.getEmail();

        ImageView chat = (ImageView) cellView.findViewById(R.id.start_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), Chat.class);
                i.putExtra("email", email);
                getContext().startActivity(i);
            }
        });

        ImageView moreInfo = (ImageView) cellView.findViewById(R.id.more_info);
        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MoreAboutTeacher.class);
                i.putExtra("email", email);

                getContext().startActivity(i);
            }
        });

        TextView name = (TextView) cellView.findViewById(R.id.teacherName);
        name.setText((String) teacher.get("Name"));

        //Check if not null!!!
        TextView teaches = (TextView) cellView.findViewById(R.id.teaches);

        List<String> relevant = new ArrayList<String>();
        List<String> needed = (List<String>) ParseUser.getCurrentUser().get("needHelp");
        List<String> teachSList = (List<String>) teacher.get("canTeach");

        for (int i = 0; i < needed.size(); i++) {
            for (int j = 0; j < teachSList.size(); j++) {
                if (needed.get(i).equals(teachSList.get(j))) {
                    relevant.add(needed.get(i));
                }
            }
        }

        teaches.setText(relevant.toString().substring(1, relevant.toString().length() - 1));

        //Check if not null!!!
        TextView education = (TextView) cellView.findViewById(R.id.education);
        education.setText((String) teacher.get("Education"));

        final ProperRatingBar ratingBar = (ProperRatingBar) cellView.findViewById(R.id.ratingBar);
        final TextView noRating = (TextView) cellView.findViewById(R.id.no_rating);

        ratingBar.setAlpha(0);
        noRating.setAlpha(0);

        ParseQuery<ParseObject> q = new ParseQuery<ParseObject>("Ratings");
        q.whereEqualTo("email", teacher.getEmail());
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

                    if(rating == 0) {
                        ratingBar.setAlpha(0);
                        noRating.setAlpha(1);
                    } else {
                        ratingBar.setAlpha(1);
                        ratingBar.setRating(rating);
                        noRating.setAlpha(0);
                    }
                }
            }
        });

        final ImageView image = (ImageView) cellView.findViewById(R.id.profile);

        ParseFile applicantResume = (ParseFile) teacher.get("Profile");
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


        SwipeLayout swipeLayout =  (SwipeLayout) cellView.findViewById(R.id.swipeList);

        //set show mode.
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
        //swipeLayout.addDrag(SwipeLayout.DragEdge.Left, cellView.findViewById(R.id.bg1));
        //swipeLayout.addDrag(SwipeLayout.DragEdge.Right, cellView.findViewById(R.id.bg2));

        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });


        return cellView;
    }
}
