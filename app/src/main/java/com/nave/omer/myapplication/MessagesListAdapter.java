package com.nave.omer.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nave.omer.myapplication.model.Conversation;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omer on 4/24/2016.
 */
public class MessagesListAdapter extends ArrayAdapter<ParseUser> {
    public MessagesListAdapter(Context context, ParseUser[] values) {
        super(context, R.layout.message_list_item, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get cell view
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View cellView = inflater.inflate(R.layout.message_list_item, parent, false);

        ParseUser user = getItem(position);

        final ImageView pro = (ImageView) cellView.findViewById(R.id.profile);

        ParseFile applicantResume = (ParseFile) user.get("Profile");
        applicantResume.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    Bitmap bitmap = ImageCircleCrop.uncompress(data);
                    pro.setImageBitmap(bitmap);
                } else {
                    pro.setImageResource(R.drawable.profile_placeholder);
                }
            }
        });

        TextView name = (TextView) cellView.findViewById(R.id.name);
        name.setText(user.get("Name").toString());

        final TextView mess = (TextView) cellView.findViewById(R.id.last);

        final TextView time = (TextView) cellView.findViewById(R.id.time);

        ParseQuery<ParseObject> q = ParseQuery.getQuery("Chat");

        ArrayList<String> al = new ArrayList<String>();

        al.add(user.getEmail());
        al.add(ParseUser.getCurrentUser().getEmail().toString());

        q.whereContainedIn("sender", al);
        q.whereContainedIn("receiver", al);
        q.orderByDescending("createdAt");
        q.setLimit(1);
        q.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> li, ParseException e) {
                if (e == null) {
                    mess.setText(li.get(0).get("message").toString());

                    String date = li.get(0).getCreatedAt().toString();
                    String timed = date.split(" ")[3];
                    String fixed = timed.substring(0, timed.length() - 3);

                    time.setText(fixed);
                }
            }
        });

        return cellView;
    }


}
