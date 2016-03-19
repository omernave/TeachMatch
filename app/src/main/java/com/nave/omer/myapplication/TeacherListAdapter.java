package com.nave.omer.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.List;

import io.techery.properratingbar.ProperRatingBar;

/**
 * Created by omer on 14/02/16.
 */
class TeacherListAdapter extends ArrayAdapter<ParseUser> {
    public TeacherListAdapter(Context context, ParseUser[] values) {
        super(context, R.layout.teacher_card, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get cell view
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View cellView = inflater.inflate(R.layout.teacher_card, parent, false);

        ParseUser teacher = getItem(position);

        TextView name = (TextView) cellView.findViewById(R.id.teacherName);
        name.setText((String) teacher.get("Name"));

        //Check if not null!!!
        TextView teaches = (TextView) cellView.findViewById(R.id.teaches);
        String teachStr = ((List<String>) teacher.get("canTeach")).toString();
        teaches.setText(teachStr.substring(1, teachStr.length() - 1));

        //Check if not null!!!
        TextView education = (TextView) cellView.findViewById(R.id.education);
        education.setText((String) teacher.get("Education"));
        
        int rating = teacher.getInt("Rating"); // get rating from parse
        ProperRatingBar ratingBar = (ProperRatingBar) cellView.findViewById(R.id.ratingBar);
        TextView noRating = (TextView) cellView.findViewById(R.id.no_rating);

        if(rating == 0) {
            ratingBar.setAlpha(0);
            noRating.setAlpha(1);
        } else {
            ratingBar.setAlpha(1);
            ratingBar.setRating(rating);
            noRating.setAlpha(0);
        }

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
