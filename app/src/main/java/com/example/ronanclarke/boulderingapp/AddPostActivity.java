package com.example.ronanclarke.boulderingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

/*
This activity will be used to add a new post once the user has recorded a time for their climb.
So the time will be carried from the TimeClimbActivity and will allow the user to put their time
as well as a picture and status about their most recent climb.
 */
public class AddPostActivity extends AppCompatActivity {
    //variables
    private Toolbar addPostTB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        addPostTB = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(addPostTB);
        getSupportActionBar().setTitle("Add New Post");


        //Implement Onclick Listener on Image for cropping image

        //Use the same code as UserProfileActivity



    }
}
