package com.nave.omer.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class RegisterScreen extends AppCompatActivity {

    public static byte[] byteBPM;
    private static int emailValidationResult = 0;
    private CircleImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        imageView = (CircleImageView) findViewById(R.id.imageView);
    }

    //Choose profile image
    public void pickImage(View view) {
        EasyImage.openChooser(this, "Choose app", true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If cropped image returned
        if (requestCode == 1231 && resultCode == 1) {
            byte[] bytes = byteBPM;
            Bitmap bmp = ImageCircleCrop.uncompress(bytes);

            imageView.setImageBitmap(bmp);

            return;
        }

        //When image picked/shot
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            //@Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                //Handle the image
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

                Intent i = new Intent(getBaseContext(), ImageCircleCrop.class);
                byteBPM = scaleDownBitmap(bitmap);
                
                startActivityForResult(i, 1231);
            }
        });
    }

    //Scale down bitmap
    public static byte[] scaleDownBitmap(Bitmap photo) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);

        return stream.toByteArray();
    }

    //Back to FirstLaunch
    public void back(View view) {
        finish();
    }

    //Go to RegisterStudent
    public void next(View view) {
        EditText name = (EditText) findViewById(R.id.name);
        EditText email = (EditText) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);

        boolean formReady = true;

        //Set errors
        name.setError(null);
        email.setError(null);
        password.setError(null);

        if (name.getText().toString().trim().length() < 4 || name.getText().toString().trim().split(" ").length <= 1) {
            name.setError("Enter your full name");
            formReady = false;
        }

        //If email form isn't valid
        if (emailFormValidator(email.getText().toString().trim()) == 0) {
            email.setError("Please enter valid email");
            formReady = false;
        }
        //If email is in use
        if (emailFormValidator(email.getText().toString().trim()) == 1) {
            email.setError("Email is already in use");
            formReady = false;
        }
        if (password.getText().toString().trim().length() < 8) {
            password.setError("At least 8 characters long");
            formReady = false;
        }

        if (formReady) {
            if (byteBPM == null) {
                Toast.makeText(getBaseContext(), "Please add an image", Toast.LENGTH_SHORT).show();
            } else {
                //Data to pass
                Intent i = new Intent(getBaseContext(), AboutYourselfRegister.class);
                i.putExtra("name", name.getText().toString().trim());
                i.putExtra("email", email.getText().toString().trim());
                i.putExtra("password", password.getText().toString());
                i.putExtra("location", getLocation());
                startActivity(i);
            }
        }
    }


    //if 0 - email format not valid
    //if 1 - email is already in use
    //if 2 - email is valid
    private int emailFormValidator(String email) {

        if (email.contains("@") && email.contains(".")) {
            String[] arr = email.split("@");

            if (arr.length == 2) {
                String sec = arr[1];
                String[] dotArr = sec.split("\\.");
                if (dotArr.length >= 2) {
                    String end = dotArr[1];

                    if (end.length() != 0) {
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereEqualTo("email", email);
                        try {
                            List<ParseUser> userList = query.find();
                            if (userList.isEmpty()) {
                                emailValidationResult = 2;
                            } else {
                                emailValidationResult = 1;
                            }
                        } catch (com.parse.ParseException e) {
                            emailValidationResult = 1;
                        }
                    }
                }
            }
        }
        return emailValidationResult;
    }

    //Hide keyboard on touch
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {}
    }

    //Get user location
    private double[] getLocation() {
        //Get user current location
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = manager.getBestProvider(new Criteria(), false);

        Location location = new Location(provider);

        //NEED TO CHECK PERMISSIONS!!!
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = manager.getLastKnownLocation(provider);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        //CHECK PERMISSIONS!!!
        double[] arr = new double[2];
        if (location != null) {
            arr = new double[] {location.getLatitude(), location.getLongitude()};
        }

        return arr;
    }
}
