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
This activity will be used to view the users profile. Here their Username, picture and posts will be shown.
The username and picture can be updated by the user if they wish to do so, the username and picture
are stored in the 'Users' collection under profile_images.
 */

public class UserProfileActivity extends AppCompatActivity
{
    //variables
    private ImageView profilePic;
    private Uri profilePicURI = null;

    private String userID;

    private boolean isChanged = false;

    private EditText usernameET;
    private Button usernameBTN;
    private ProgressBar progressPB;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Toolbar at top of screen
        Toolbar setupToolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(setupToolbar);

        getSupportActionBar().setTitle("My Account");

        mAuth = FirebaseAuth.getInstance();
        //get user id from Firebase Authentication variable
        userID = mAuth.getCurrentUser().getUid();

        //Make instance of firestore database and firestore storage file
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        profilePic = findViewById(R.id.profile_picture);
        usernameET = findViewById(R.id.profile_name);
        usernameBTN = findViewById(R.id.update_profile_button);
        progressPB = findViewById(R.id.profile_progress);

        progressPB.setVisibility(View.VISIBLE);
        usernameBTN.setEnabled(false);

        //Check Users collection in the Firebase firestore database
        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                //If Users exists
                if(task.isSuccessful())
                {
                    //Where there exists data  witihin Users
                    if(task.getResult().exists()){

                        //create local variables to hold the username and image
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        //Make connection with firebase image URL in order to make changes
                        profilePicURI = Uri.parse(image);

                        //User sets their username alongside their profile pic
                        usernameET.setText(name);

                        //Display the image to hold place of image - e.g default image
                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.ic_launcher_background);
                        //load the image into setupImage
                        Glide.with(UserProfileActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(profilePic);


                    }

                }
                else //If Users collection isn't found exist
                {

                    String error = task.getException().getMessage();
                    //Display error message
                    Toast.makeText(UserProfileActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }

                progressPB.setVisibility(View.INVISIBLE);
                usernameBTN.setEnabled(true);

            }
        });

        //Listen on setup button for on click
        usernameBTN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //create local variable to hold the username
                final String user_name = usernameET.getText().toString();

                //Check if username is filled out and that image has been selected
                if (!TextUtils.isEmpty(user_name) && profilePicURI != null)
                {

                    progressPB.setVisibility(View.VISIBLE);

                    //check whether the username has been changed from the default
                    if (isChanged)
                    {
                        //get current user id
                        userID = mAuth.getCurrentUser().getUid();

                        //Make connection to profile images within Users collection
                        StorageReference image_path = storageReference.child("profile_images").child(userID + ".jpg");
                        //Use the image url to put the username alongside the image in profile images collection
                        image_path.putFile(profilePicURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                            {
                                //If successfully connected
                                if (task.isSuccessful())
                                {
                                    //add the username to Firestore
                                    firestoreHandler(task, user_name);

                                }
                                //If connection not made successfully
                                else
                                {

                                    String error = task.getException().getMessage();
                                    //Display error message
                                    Toast.makeText(UserProfileActivity.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                                    progressPB.setVisibility(View.INVISIBLE);

                                }
                            }
                        });

                    }
                    //User has not changed their username
                    else
                    {
                        //Call function storeFirestore, send null to specify that
                        firestoreHandler(null, user_name);

                    }

                }

            }

        });

        //Listen for click on user pic
        profilePic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Check if user system is higher than marshmellow, then you need to ask user for permission to read gallery
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    //If user has not already allowed permission to gallery
                    if(ContextCompat.checkSelfPermission(UserProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        //Display error message
                        Toast.makeText(UserProfileActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        //Prompt user to authorise permission to gallery
                        ActivityCompat.requestPermissions(UserProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

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

            }

        });


    }

    //Handler for Firestore
    private void firestoreHandler(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name)
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

            download_uri = profilePicURI;

        }

        //Encrypt the information
        Map<String, String> userMap = new HashMap<>();

        userMap.put("name", user_name);
        userMap.put("image", download_uri.toString());

        //Set new username and user image using users id
        firebaseFirestore.collection("Users").document(userID).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                //If username successfully added to FirebaseFirestore
                if(task.isSuccessful())
                {
                    //Let user know their settings are updated
                    Toast.makeText(UserProfileActivity.this, "The user Settings are updated.", Toast.LENGTH_LONG).show();
                    //Send user to the main Activity
                    Intent mainIntent = new Intent(UserProfileActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                }
                //If username not successfully added to FirebaseFireStore
                else
                {

                    String error = task.getException().getMessage();
                    //Display Error message
                    Toast.makeText(UserProfileActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                }

                progressPB.setVisibility(View.INVISIBLE);

            }
        });


    }

    //function to crop image
    private void cropImage() {

        //Use external library to crop image
        CropImage.activity()
                //Gives user grid to specify size of image
                .setGuidelines(CropImageView.Guidelines.ON)
                //Keeps the ratio of the photo so that remains a perfect square
                .setAspectRatio(1, 1)
                .start(UserProfileActivity.this);

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
                profilePicURI = result.getUri();
                profilePic.setImageURI(profilePicURI);

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

/*---------------------------------------

References:
- Using firestore
https://firebase.google.com/docs/firestore/quickstart

 */