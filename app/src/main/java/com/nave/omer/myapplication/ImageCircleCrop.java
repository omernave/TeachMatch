package com.nave.omer.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.isseiaoki.simplecropview.CropImageView;

public class ImageCircleCrop extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_circle_crop);

        byte[] byteBPM = RegisterScreen.byteBPM;

        Bitmap bitmapToCrop = uncompress(byteBPM);

        final CropImageView cropImageView = (CropImageView)findViewById(R.id.cropImageView);

        // Set image for cropping
        cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
        cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
        cropImageView.setImageBitmap(bitmapToCrop);

        Button cropButton = (Button) findViewById(R.id.crop_button);
        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get cropped image, and show result.
                Bitmap bitmap = (cropImageView.getCroppedBitmap());

                RegisterScreen.byteBPM = RegisterScreen.scaleDownBitmap(bitmap);

                Intent returnIntent = new Intent();
                setResult(1, returnIntent);

                finish();
            }
        });
    }

    public static Bitmap uncompress(byte[] a){
        return BitmapFactory.decodeByteArray(a, 0, a.length);
    }

}
