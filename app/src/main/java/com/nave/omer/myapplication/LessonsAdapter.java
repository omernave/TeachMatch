package com.nave.omer.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by omer on 4/27/2016.
 */
public class LessonsAdapter extends ArrayAdapter<String[]> {

    String email = "";

    public LessonsAdapter(Context context, String[][] values) {
        super(context, R.layout.future_meetings_item, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get cell view
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View cellView = inflater.inflate(R.layout.future_meetings_item, parent, false);
        final String[] data = getItem(position);

        //Set navigation button
        ImageView navi = (ImageView) cellView.findViewById(R.id.navigate);
        navi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Google Maps on location user entered
                String loc = data[4].replace(" ", "+");
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + loc);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                getContext().startActivity(mapIntent);
            }
        });

        //Setup TextViews
        TextView sub = (TextView) cellView.findViewById(R.id.subject);
        TextView teacher = (TextView) cellView.findViewById(R.id.teacher);
        TextView rate = (TextView) cellView.findViewById(R.id.rate);
        TextView date = (TextView) cellView.findViewById(R.id.date);
        TextView time = (TextView) cellView.findViewById(R.id.time);
        TextView loca = (TextView) cellView.findViewById(R.id.location);

        sub.setText(data[0] + " lesson");
        teacher.setText(data[1]);
        rate.setText("$" + data[5] + "/Hour");
        date.setText(data[2]);
        time.setText(data[3]);
        loca.setText(data[4]);

        return cellView;
    }
}
