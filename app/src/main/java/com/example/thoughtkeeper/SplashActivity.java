package com.example.thoughtkeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    //it sets the content view to activity_splash.xml
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //uses a Handler to delay the execution of a task by 1000 milliseconds (1 second)
        //runnable inside the postDelayed method will run after the delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //it retrieves the current user from Firebase Authentication using FirebaseAuth.getInstance().getCurrentUser().
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                //if user is not logged in
                if(currentUser==null){
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                    //if user is logged in
                }else {
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                }
                //close the SplashActivity
                finish();
            }
        }, 1000);
    }
}