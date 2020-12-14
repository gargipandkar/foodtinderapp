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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SignOutActivity extends AppCompatActivity implements ProfileFragment.FragmentProfileListener, HomeFragment.FragmentHomeListener, GroupFragment.FragmentGroupListener, CreateEventFragment.CreateEventFragmentListener, WaitingEventFragment.FragmentWaitingEventListener, WaitingGroupFragment.FragmentWaitingGroupListener, CreateGroupFragment.CreateGroupFragmentListener, WaitingRestaurantFragment.FragmentWaitingRestaurantListener, SwipingFragment.SwipingFragmentListener, SwipeTestFragment.SwipeTestFragmentListener {

    // TAG for debugging purposes
    public static final String TAG = "SignOutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super .onCreate(savedInstanceState);
        setContentView(R.layout.activity_signout);

        // Set bottom navigation view in this activity
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Default Fragment to display when in this activity
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WaitingEventFragment()).commit();

        boolean fromDynamicLink = getIntent().getBooleanExtra("EXTRA_JOINED_GROUP", false);
        if (fromDynamicLink == true){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WaitingGroupFragment()).commit();
        }

    }

    // Decides what each bottom navigation navigates to
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = new WaitingEventFragment();

                    switch (item.getItemId()){
                        case R.id.navigation_home:
                            selectedFragment = new WaitingEventFragment();
                            break;
                        case R.id.navigation_group:
                            selectedFragment = new WaitingGroupFragment();
                            break;
                        case R.id.navigation_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };


    // Function to call from Profile Fragment to sign out user
    @Override
    public void onLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent toSignIn = new Intent(SignOutActivity.this, SignInActivity.class);
        startActivity(toSignIn);
        finish();
    }

    // Function to call from Home Fragment when user click the button to create an event form
    // Opens CreateEventFragment to display event creation form
    @Override
    public void onCreateEvent() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateEventFragment()).addToBackStack(null).commit();
    }

    // Function to call from WaitingRestaurant Fragment to get the list of restaurants from Google Places API and Firebase Realtime Database
    // Prevents the app from crashing by retrieving the list before proceeding to Swiping Fragment where user swipe to choose their restaurant preference
    // Opens SwipeTest Fragment
    @Override
    public void onListingRestaurant(String event_id, ArrayList<String> restAddr, ArrayList<String> restName, HashMap<String, ArrayList<String>> listRestPhotos, Object[] listRestNames, HashMap<String, Integer> listRestVotes) {
        Fragment toSwipe = new SwipeTestFragment();
        Bundle bundle = new Bundle();
        bundle.putString("eventId", event_id);
        bundle.putStringArrayList("name", restName);
        bundle.putStringArrayList("addr", restAddr);
        bundle.putSerializable("photos", listRestPhotos);
        bundle.putSerializable("listRestNames", listRestNames);
        bundle.putSerializable("listRestVotes", listRestVotes);
        toSwipe.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, toSwipe).commit();
    }


    // Function to call from Group Fragment when user click the button to create a group form
    // Opens CreateGroup Fragment
    @Override
    public void onCreateGroup() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateGroupFragment()).addToBackStack(null).commit();
    }


    // Function to call from CreateEvent Fragment when user creates an event from the event creation form
    // Proceeds to WaitingEvent Fragment which updates the new event onto Firebase before returning back to Home Fragment which list the updated list of events
    // Opens WaitingEvent Fragment
    @Override
    public void onNewEventUpdate() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WaitingEventFragment()).commit();
    }


    // Function to call from WaitingEvent Fragment to get the list of events from Firebase Realtime Database
    // Prevents the app from crashing by retrieving the list before proceeding to Home Fragment
    // Opens Home Fragment
    @Override
    public void onListingEvents(ArrayList<Event> eventArrayList) {
        if (eventArrayList.size() > 0){
            // go to Home fragement with list of events
            Fragment home = new HomeFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("eventlist", eventArrayList);
            home.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, home).commit();
            Log.i(TAG, "sending list to waiting fragment");
            Log.i(TAG, eventArrayList.toString());
        }
        else {
            // go to Home fragment with empty list
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }


    }

    // Function to call from WaitingGroup Fragment to get the list of groups from Firebase Realtime Database
    // Prevents the app from crashing by retrieving the list before proceeding to Group Fragment
    // Opens Group Fragment
    @Override
    public void onListingGroup(ArrayList<Group> groupArrayList) {
        if (groupArrayList.size() > 0){
            // go to Group fragement with list of events
            Fragment group = new GroupFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("grouplist", groupArrayList);
            group.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, group).commit();
        } else {
            // go to Group fragment with empty list
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GroupFragment()).commit();
        }

    }

    // Function to call from CreateGroup Fragment when user creates a group from the group creation form
    // Proceeds to WaitingGroup Fragment which updates the new group onto Firebase before returning back to Group Fragment which list the updated list of groups
    // Opens WaitingEvent Fragment
    @Override
    public void onNewGroupUpdate() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WaitingGroupFragment()).commit();
    }


    // Function to call from Home Fragment when user clicks on an event in Home Fragment
    // Opens WaitingRestaurant Fragment to retrieve all restaurant from Google Places API and Firebase Realtime Database
    // before proceeding to SwipeTest Fragment
    @Override
    public void selectRestaurant(String eventId, boolean firstEntry) {
        // Go to Retrieving Restaurant Fragment
        // TODO: Remove this if button clicking to select restaurant preference is not used
//        Fragment toRetrieveRest = new SwipingFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("eventId", eventId);
//        bundle.putBoolean("firstEntry", firstEntry);
//        toRetrieveRest.setArguments(bundle);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, toRetrieveRest).commit();


        Fragment toWaiting = new WaitingRestaurantFragment();
        Bundle bundle = new Bundle();
        bundle.putString("eventId", eventId);
        bundle.putBoolean("firstEntry", firstEntry);
        toWaiting.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, toWaiting).commit();
    }

    // TODO: Remove this if button clicking to select restaurant preference is not used
    @Override
    public void updateRest(String event_id, int number, HashMap<String, Integer> listRestVotes, boolean checkLastItem) {
        if (number < listRestVotes.size() - 1) {
            Fragment toSwipe = new SwipingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("eventId", event_id);
            bundle.putInt("num", number);
            bundle.putSerializable("votes", listRestVotes);
            bundle.putBoolean("checkLastItem", checkLastItem);
            toSwipe.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, toSwipe).commit();
        } else if (number == listRestVotes.size() - 1) {
            checkLastItem = true;
            Fragment toSwipe = new SwipingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("eventId", event_id);
            bundle.putInt("num", number);
            bundle.putSerializable("votes", listRestVotes);
            bundle.putBoolean("checkLastItem", checkLastItem);
            toSwipe.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, toSwipe).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WaitingEventFragment()).commit();
        }

    }

    // Function to call from SwipeTest Fragment when user has completed swiping through all restaurants
    // Opens WaitingEvent Fragment to retrieve the updated list of events user is in
    // before proceeding to Home Fragment
    @Override
    public void finishSwipe() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WaitingEventFragment()).commit();
    }


}
