package com.example.ronanclarke.boulderingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
This activity will be used to sign the user into the application. The sign in method is implemented
using Firebase authentication module. The authentication used here is sign in with email and
password so the user must provide their username and password. This is then verfified if it
exists in the firebase authentication.
If the user exists and password is correct, they can access the rest of the app.
If the user does not have a registered account, they should register a new account by
clicking the register account button which will redirect them to user registration activity.
 */

public class SignInActivity extends AppCompatActivity
{

    //Declare variables
    private EditText emailET;
    private EditText passwordET;
    private Button signInBTN;
    private Button registerBTN;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Initialise Firebase authentication
        mAuth = FirebaseAuth.getInstance();

        emailET = findViewById(R.id.email_register);
        passwordET = findViewById(R.id.password_register);
        signInBTN = findViewById(R.id.button_signIn);
        registerBTN = findViewById(R.id.button_register);


        //Add onClick Listener to register account button
        registerBTN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Onclick send to Register Activity class
                Intent regIntent = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(regIntent);

            }
        });

        //Onclick Login
        signInBTN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Get the test thats in the two fields email and password
                String loginEmail = emailET.getText().toString();
                String loginPass = passwordET.getText().toString();

                //Check if the two fields are not empty
                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)){
                    //sign in with email and password and pass in email and password
                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {

                            if(task.isSuccessful())
                            {
                                //call function sendtoMain in order to send to MainActivity()
                                sendToMain();

                            }
                            else
                            {
                                //Send error message to user
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(SignInActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();


                            }


                        }
                    });

                }


            }
        });


    }
    //While app is running need to check whether user is still logged in
    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {

            sendToMain();

        }


    }

    private void sendToMain()
    {
        //User is now sent to Main activity
        Intent mainIntent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }
}