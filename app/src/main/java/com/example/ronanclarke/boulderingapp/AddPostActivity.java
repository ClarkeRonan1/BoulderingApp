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
import java.util.UUID;

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
    protected void onCreate(Bundle savedInstanceState) {
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
        climbWallPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //If user has not already allowed permission to gallery
                    if (ContextCompat.checkSelfPermission(AddPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        //Display error message
                        Toast.makeText(AddPostActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        //Prompt user to authorise permission to gallery
                        ActivityCompat.requestPermissions(AddPostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    }
                    //Open image picker
                    else {
                        //Call function in order to crop photo
                        cropImage();

                    }

                }
                // Open image picker if sdk is less than Marshmallow
                else {
                    //Call function in order to crop photo
                    cropImage();

                }
                //Call function cropImage
                cropImage();


            }
        });

        //Copied and pasted from UserProfileActivity
        //Listen on setup button for on click
        addPostBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create local variable to hold the user Status
                final String user_status = userStatus.getText().toString();

                //Check if username is filled out and that image has been selected
                if (!TextUtils.isEmpty(user_status) && climbWallPicURI != null) {


                    //check whether the username has been changed from the default
                    if (isChanged) {
                        //get current user id
                        userID = mAuth.getCurrentUser().getUid();

                        //Make connection to profile images within Users collection
                        StorageReference storedFiles = storageReference.child("route_images").child(userID + ".jpg");
                        //Use the image url to put the username alongside the image in profile images collection
                        storedFiles.putFile(climbWallPicURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                //If successfully connected
                                if (task.isSuccessful()) {
                                    //add the username to Firestore
                                    firestoreHandler(task, user_status);

                                }
                                //If connection not made successfully
                                else {

                                    String error = task.getException().getMessage();
                                    //Display error message
                                    Toast.makeText(AddPostActivity.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();


                                }
                            }
                        });

                    }
                    //User has not changed their username
                    else {
                        //Call function storeFirestore, send null to specify that
                        firestoreHandler(null, user_status);

                    }

                }

            }

        });

    }
    //Copied and pasted from UserProfileActivity

    //Handler for Firestore
    private void firestoreHandler(@NonNull Task<UploadTask.TaskSnapshot> task, String userStatus)
    {
        //get firestore URL
        Uri download_uri;

        //Task needs to be carried out so get URL, this case need to add username
        if(task != null)
        {

            download_uri = task.getResult().getDownloadUrl();

        }
        //No task to be carried out, so just download the information
        else
        {

            download_uri = climbWallPicURI;

        }

        //Encrypt the information
        Map<String, String> userMap = new HashMap<>();

        userMap.put("name", userStatus);
        userMap.put("image", download_uri.toString());

        //Set new username and user image using users id
        firebaseFirestore.collection("Posts").document(userID).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                //If username successfully added to FirebaseFirestore
                if(task.isSuccessful())
                {
                    //Let user know their settings are updated
                    Toast.makeText(AddPostActivity.this, "The user Settings are updated.", Toast.LENGTH_LONG).show();
                    //Send user to the main Activity
                    Intent mainIntent = new Intent(AddPostActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                }
                //If username not successfully added to FirebaseFireStore
                else
                {

                    String error = task.getException().getMessage();
                    //Display Error message
                    Toast.makeText(AddPostActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                }


            }
        });


    }
        /*
        //Onclick listener for add new post button
        addPostBTN.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                //Take user input for status
                final String status = userStatus.getText().toString();

                //Check if user status input filled
                if(!TextUtils.isEmpty(status))
                {
                    //Give this status a unique ID
                    final String assignID = UUID.randomUUID().toString();
                    //Reference to the storage file in Firebase, check within Storage for
                    //The name of the file, here is the profile_images and route_images
                    StorageReference storedFiles = storageReference.child("route_images");
                    storedFiles.putFile(climbWallPicURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                        {
                            final String UriCheck = task.getResult().getDownloadUrl().toString();
                            //if climbWallPucURI is valid url
                            if(task.isSuccessful())
                            {

                            }
                        }
                    })

                }
            }
        });
        */



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
