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
import androidx.fragment.app.FragmentTransaction;

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

import java.util.ArrayList;
import java.util.Calendar;

public class SignOutActivity extends AppCompatActivity implements ProfileFragment.FragmentProfileListener, HomeFragment.FragmentHomeListener, GroupFragment.FragmentGroupListener, CreateEventFragment.CreateEventFragmentListener, WaitingEventFragment.FragmentWaitingEventListener, WaitingGroupFragment.FragmentWaitingGroupListener{

//    public static Fragment selectedFragment = new HomeFragment();

    public static final String TAG = "SignOutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super .onCreate(savedInstanceState);
        setContentView(R.layout.activity_signout);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WaitingEventFragment()).commit();


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    String fragTag;

                    switch (item.getItemId()){
                        case R.id.navigation_home:
                            selectedFragment = new WaitingEventFragment();
//                            selectedFragment = new HomeFragment();
                            fragTag = "home";
                            break;
                        case R.id.navigation_group:
                            selectedFragment = new WaitingGroupFragment();
//                            selectedFragment = new GroupFragment();
                            fragTag = "group";
                            break;
                        case R.id.navigation_profile:
                            selectedFragment = new ProfileFragment();
                            fragTag = "profile";
                            break;
                        default:
                            fragTag = "home";
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
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, creatEventFrag).addToBackStack(null).commit();
        Log.i(TAG, "Fragment initialised");
//        Intent toCreateEvent = new Intent(SignOutActivity.this, CreateEventActivity.class);
//        startActivity(toCreateEvent);
    }

    @Override
    public void updateHome(boolean checkListNotEmpty, Fragment curFrag) {
        if (checkListNotEmpty == true){
            // detach and reattach fragment to reload home fragment
//            Fragment frg = null;
//            frg = getSupportFragmentManager().findFragmentByTag("home");
//            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.detach(frg);
//            ft.attach(frg);
//            ft.commit();
//            if (currentFragment instanceof "NAME OF YOUR FRAGMENT CLASS") {
//                FragmentTransaction fragTransaction =   (getActivity()).getFragmentManager().beginTransaction();
//                fragTransaction.detach(currentFragment);
//                fragTransaction.attach(currentFragment);
//                fragTransaction.commit();}
            getSupportFragmentManager().beginTransaction().remove(curFrag).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }



    }

    @Override
    public void onCreateGroup() {
        Intent toCreateGroup = new Intent(SignOutActivity.this, CreateGroupActivity.class);
        startActivity(toCreateGroup);
    }


//    @Override
//    public void onNewEvent(boolean bool, int eventId, String name, String group, String userId, Long dateTime, String budget, String location, String status) {
//        if (bool == true){
//            Log.i(TAG, name);
//            Fragment home = new HomeFragment();
//            Bundle bundle = new Bundle();
//            bundle.putInt("eventId", eventId);
//            bundle.putString("name", name);
//            bundle.putString("group", group);
//            bundle.putString("userId", userId);
//            bundle.putLong("dateTime", dateTime);
//            bundle.putString("budget", budget);
//            bundle.putString("location", location);
//            bundle.putString("status", status);
//            home.setArguments(bundle);
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, home).commit();
//            Log.i(TAG, "sending event details");
//        }
//        else {
//            Toast.makeText(getApplicationContext(), "Ensure all details are filled up", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void onNewEventUpdate() {
        Log.i(TAG, "onNewEventUpdate()");
//        Fragment waitingEvent = new WaitingEventFragment();
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, waitingEvent).commit();
//        Log.i(TAG, "WaitingEvent Fragment initialised");
//        Intent test = new Intent(SignOutActivity.this, TestEvent.class);
//        startActivity(test);
    }

    @Override
    public void onListingEvents(ArrayList<Event> eventArrayList) {
        if (eventArrayList.size() > 0){
            Log.i(TAG, "onListingEvents");
            // go to Home fragement with list of events
            Fragment home = new HomeFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("eventlist", eventArrayList);
            home.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, home).commit();
            Log.i(TAG, "sending event list");
        } else {
            // go to Home fragment with empty list
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

        }
    }

    @Override
    public void onListingGroup(ArrayList<Group> groupArrayList) {
        if (groupArrayList.size() > 0){
            // go to Group fragement with list of events
            Fragment group = new GroupFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("grouplist", groupArrayList);
            group.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, group).commit();
            Log.i(TAG, "sending event list");
        } else {
            // go to Group fragment with empty list
        }
    }
}
