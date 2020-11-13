package com.example.foodtinder;

import android.os.Handler;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //code starts here
        Button enter_btn = findViewById(R.id.enter_btn);
//        enter_btn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Log.i("Check", "Button clicked");
//                Intent toCreateEvent = new Intent(MainActivity.this, SignInActivity.class);
//                startActivity(toCreateEvent);
//            }
//        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if ()
//            }
//        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {

        if (currentUser != null){
            // User is signed in
            Intent toListEvent = new Intent (MainActivity.this, SignOutActivity.class);
            startActivity(toListEvent);
            finish();
        } else {
            // No user is signed in
            Intent toSignIn = new Intent (MainActivity.this, SignInActivity.class);
            startActivity(toSignIn);
            finish();
        }
    }
}
