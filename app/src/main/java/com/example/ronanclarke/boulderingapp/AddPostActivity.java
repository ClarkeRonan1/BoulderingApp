package com.example.ronanclarke.boulderingapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/*
This activity will be used to add a new post once the user has recorded a time for their climb.
So the time will be carried from the TimeClimbActivity and will allow the user to put their time
as well as a picture and status about their most recent climb.
 */
public class AddPostActivity extends AppCompatActivity
{
    //variables
    private Toolbar addPostTB;
    private Uri climbWallPicURI = null;

    private String userID;

    private boolean isChanged = false;

    //Input fields
    private ImageView climbWallPic;        //This will be used to hold photo of climbing wall
    private EditText routeName; //Holds the route name
    private EditText userTime;  //Holds time taken to climb wall
    private EditText userStatus;    //User comments on route

    private Button addPostBTN;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        addPostTB = findViewById(R.id.add_post_toolbar);
        setSupportActionBar(addPostTB);
        //Display Add new post at top of screen
        getSupportActionBar().setTitle("Add New Post");

        //Instance of Firebase auth and current user
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        //Instance of firestore database and cloudstorage
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //Set buttons/editexts to xml
        climbWallPic = findViewById(R.id.route_image);
        routeName = findViewById(R.id.route_name);
        userTime = findViewById(R.id.user_time);
        userStatus = findViewById(R.id.user_status);
        addPostBTN = findViewById(R.id.add_post_button);

        //Implement Onclick Listener on Image for cropping image
        climbWallPic.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    //If user has not already allowed permission to gallery
                    if(ContextCompat.checkSelfPermission(AddPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        //Display error message
                        Toast.makeText(AddPostActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        //Prompt user to authorise permission to gallery
                        ActivityCompat.requestPermissions(AddPostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    }
                    //Open image picker
                    else
                    {
                        //Call function in order to crop photo
                        cropImage();

                    }

                }
                // Open image picker if sdk is less than Marshmallow
                else
                {
                    //Call function in order to crop photo
                    cropImage();

                }
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(AddPostActivity.this);

            }
        });
        //Use the same code as UserProfileActivity



    }

    //function to crop image
    private void cropImage()
    {

        //Use external library to crop image
        CropImage.activity()
                //Gives user grid to specify size of image
                .setGuidelines(CropImageView.Guidelines.ON)
                //Keeps the ratio of the photo so that remains a perfect square
                .setAspectRatio(1, 1)
                .start(AddPostActivity.this);
    }

    //Result of Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        //Check whether the user chose to crop image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            //If the cropping worked
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                //update the original default image
                climbWallPicURI = result.getUri();
                climbWallPic.setImageURI(climbWallPicURI);

                //Set ischanged to True as the image has been changed from
                //the original default image
                isChanged = true;

            }
            //If the crop image did not work successfully
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                //Throw error
                Exception error = result.getError();

            }
        }

    }
}
