package com.example.ronanclarke.boulderingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/*
This activity will be used to display the users home page. This is where the user can
time climbs and in turn add new posts to the app. The user also has access to their profile and
the option to sign out of their account.
When the user presses the add new post button, this calls the addNewPost activity where a new post will
be constructed by the user.
 */

public class MainActivity extends AppCompatActivity
{


    //variables
    private Toolbar homeTB;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String currentUserID;

    private FloatingActionButton addPostBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Make instance of firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //Make instance of firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Set toolbar with name photo blog
        homeTB = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(homeTB);

        getSupportActionBar().setTitle("Photo Blog");

        addPostBTN = findViewById(R.id.add_post_button);
        //Listen for button click on make new post button
        addPostBTN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Send user to activity for making post
                Intent newPostIntent = new Intent(MainActivity.this, AddPostActivity.class);
                startActivity(newPostIntent);

            }
        });


    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //If there isn't a user signed in
        if(currentUser == null)
        {
            //Send this user to the login page so they can sign in
            returnToSignIn();

        }
        else
        {
            //verify the current user
            currentUserID = mAuth.getCurrentUser().getUid();
            //using users id, make a connection to the Users collection
            firebaseFirestore.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                    if(task.isSuccessful())
                    {

                        if(!task.getResult().exists())
                        {
                            //Send the user to the Setup Activity
                            Intent setupIntent = new Intent(MainActivity.this, UserProfileActivity.class);
                            startActivity(setupIntent);
                            finish();

                        }

                    }
                    else //If task is unsuccessful
                    {
                        //Display error message
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();


                    }

                }
            });

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //Inflate the main menu toolbar
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //Switch case statement to determine which option is chosen
        switch (item.getItemId())
        {
            //where logout pressed
            case R.id.logout_button:
                //call function log out
                signOut();
                return true;
            //where user settings pressed
            case R.id.settings_button:
                //send user to the setup activity
                Intent settingsIntent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(settingsIntent);

                return true;

            //Otherwise neither of the buttons have been pressed
            default:
                return false;


        }

    }
    //Function for logging out the user
    private void signOut()
    {

        //Use firebase authorisation to signout user
        mAuth.signOut();
        //Call function to send user back to login activity
        returnToSignIn();
    }
    //Function to send user to login activity
    private void returnToSignIn()
    {
        //Send user to Login Activity
        Intent loginIntent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(loginIntent);
        finish();

    }

}