package com.example.foodtinder;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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

//    SharedPreferences prefs;
//    private static final String SHARED_PREF_NAME = "prefs";
//    private static final String KEY_NAME = "name";
//    private static final String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();



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
            Intent toHome = new Intent (MainActivity.this, SignOutActivity.class);

            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();

//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putString(KEY_NAME, name);
//            editor.putString(KEY_EMAIL, email);
//            editor.apply();


//            Uri photoUrl = currentUser.getPhotoUrl();
//            String photoStr = photoUrl.toString();
//            User user = new User(name, email);

            startActivity(toHome);
            finish();
        } else {
            // No user is signed in
            Intent toSignIn = new Intent (MainActivity.this, SignInActivity.class);
            startActivity(toSignIn);
            finish();
        }
    }
}
