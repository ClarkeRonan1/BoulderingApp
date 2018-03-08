package com.example.ronanclarke.boulderingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
/*
This activity will be used to time the user while they climb the wall. There will be a button to
start the timer, the user will start climbing and when the user reaches the top of the climbing wall
they    A: press the stop button
        B: jump off to trigger the motion sensor and stop the timer
This time will be then recorded and the user will be directed to the add new post activity where
the user can take a picture of the climbing wall and write a post on the route they just climbed.
 */
public class TimeClimbActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_climb);
    }
}
