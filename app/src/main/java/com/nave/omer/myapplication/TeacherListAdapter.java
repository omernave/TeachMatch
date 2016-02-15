package com.nave.omer.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by omer on 14/02/16.
 */
class TeacherListAdapter extends ArrayAdapter<TeacherObject> {
    public TeacherListAdapter(Context context, TeacherObject[] values) {
        super(context, R.layout.teacher_card, values);
    }

    //UNFINISHED!!!!!!!!!!!!!
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get cell view
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View cellView = inflater.inflate(R.layout.teacher_card, parent, false);

        TeacherObject teacher = getItem(position);

        TextView name = (TextView) cellView.findViewById(R.id.teacherName);
        name.setText(teacher.getName());

        //Parse var might be List or Array!!!
        TextView teaches = (TextView) cellView.findViewById(R.id.teaches);
        teaches.setText(teacher.getTeaches().toString());

        TextView learns = (TextView) cellView.findViewById(R.id.learns);
        teaches.setText(teacher.getLearns().toString());

        /* Add RatingBar
        TextView rate = (TextView) cellView.findViewById(R.id.rate);
        teaches.setText(teacher.getRating());
        */

        ImageView image = (ImageView) cellView.findViewById(R.id.profile);
        Bitmap bitmap = ImageCircleCrop.uncompress(teacher.getProfile());
        image.setImageBitmap(bitmap);

        return cellView;
    }
}
