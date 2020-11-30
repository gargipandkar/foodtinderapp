package com.example.foodtinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Calendar;

public class SignOutActivity extends AppCompatActivity implements ProfileFragment.FragmentProfileListener, HomeFragment.FragmentHomeListener, GroupFragment.FragmentGroupListener, CreateEventFragment.CreateEventFragmentListener{

//    public static Fragment selectedFragment = new HomeFragment();

    public static final String TAG = "SignOutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super .onCreate(savedInstanceState);
        setContentView(R.layout.activity_signout);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.navigation_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.navigation_group:
                            selectedFragment = new GroupFragment();
                            break;
                        case R.id.navigation_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };

    @Override
    public void onLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent toSignIn = new Intent(SignOutActivity.this, SignInActivity.class);
        startActivity(toSignIn);
        finish();
    }

    @Override
    public void onCreateEvent() {
        Fragment creatEventFrag = new CreateEventFragment();
        Log.i(TAG, "Fragment initialising");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, creatEventFrag).commit();
        Log.i(TAG, "Fragment initialised");
//        Intent toCreateEvent = new Intent(SignOutActivity.this, CreateEventActivity.class);
//        startActivity(toCreateEvent);
    }

    @Override
    public void onCreateGroup() {
        Intent toCreateGroup = new Intent(SignOutActivity.this, CreateGroupActivity.class);
        startActivity(toCreateGroup);
    }


    @Override
    public void onNewEvent(boolean bool, String name, Long dateTime, String budget, String location, String status) {
        if (bool == true){
            Log.i(TAG, name);
            Fragment home = new HomeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            bundle.putLong("dateTime", dateTime);
            bundle.putString("budget", budget);
            bundle.putString("location", location);
            bundle.putString("status", status);
            home.setArguments(bundle);
            Log.i(TAG, "string");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, home).commit();
            Log.i(TAG, "string");
        }
        else {
            Toast.makeText(getApplicationContext(), "Ensure all details are filled up", Toast.LENGTH_SHORT).show();
        }
    }
}
