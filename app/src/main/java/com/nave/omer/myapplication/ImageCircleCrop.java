package com.nave.omer.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.isseiaoki.simplecropview.CropImageView;

import java.io.ByteArrayOutputStream;

public class ImageCircleCrop extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_circle_crop);

        //Setup ImageView
        Bitmap bitmapToCrop = uncompress(RegisterScreen.byteBPM);
        final CropImageView cropImageView = (CropImageView)findViewById(R.id.cropImageView);

        // Set image for cropping
        cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
        cropImageView.setImageBitmap(bitmapToCrop);

        Button cropButton = (Button) findViewById(R.id.crop_button);
        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get cropped image, and show result.
                Bitmap bitmap = (cropImageView.getCroppedBitmap());
                //Fix image rotation problem
                Bitmap fixed = rotateBitmap(bitmap, 270);
                RegisterScreen.byteBPM = RegisterScreen.scaleDownBitmap(fixed);

                Intent returnIntent = new Intent();
                setResult(1, returnIntent);

                finish();
            }
        });
    }

    //Rotate image
    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    //Uncompress Bitmap
    public static Bitmap uncompress(byte[] a){
        return BitmapFactory.decodeByteArray(a, 0, a.length);
    }

}
