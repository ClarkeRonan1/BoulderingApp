package com.example.ronanclarke.boulderingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/*
This activity will be used to register new users via email and password authentication. The user
must enter an email and password and this will create a new account for the user so that they
may access the other features of the app. This saves their account for the next time they want to
sign in so they simply use the SignInActivity the following time.
 */
public class RegisterActivity extends AppCompatActivity
{
    //Variables
    private EditText registerEmailET;
    private EditText registerPasswordET;
    private Button registerBTN;
    private Button signInBTN;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //get instance of firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //assign the variables to the ids in xml
        registerEmailET = findViewById(R.id.register_email);
        registerPasswordET = findViewById(R.id.register_password);
        registerBTN = findViewById(R.id.register_button);
        signInBTN = findViewById(R.id.register_signIn_button);

        //Listen for click on return to login button
        signInBTN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Call function finish() will end the intent from LoginActivity and return there
                finish();

            }
        });

        // Listen for click on register the new user button
        registerBTN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Assign variable to store inout from user
                String email = registerEmailET.getText().toString();
                String pass = registerPasswordET.getText().toString();


                //Check if the fields have been filled
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass))
                {
                    //use function to create new user with email and password
                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            //Check if this operation worked
                            if(task.isSuccessful())
                            {
                                //Send user to Setup Activity
                                Intent setupIntent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                                startActivity(setupIntent);
                                finish();

                            }
                            //If operation of createUserWithEmailAndPassword is unsuccessful
                            else
                            {

                                String errorMessage = task.getException().getMessage();
                                //Display error message
                                Toast.makeText(RegisterActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                            }


                        }
                    });

                }
                else //If th two fields are not filled
                {
                    //Display message to user
                    Toast.makeText(RegisterActivity.this, "Confirm Password and Password Field doesn't match.", Toast.LENGTH_LONG).show();

                }
            }



        });


    }

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
        //Send user to mainActivity now and finish the original intent
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }
}

