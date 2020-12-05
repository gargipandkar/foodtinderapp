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

public class SignOutActivity extends AppCompatActivity implements ProfileFragment.FragmentProfileListener, HomeFragment.FragmentHomeListener, GroupFragment.FragmentGroupListener, CreateEventFragment.CreateEventFragmentListener, WaitingEventFragment.FragmentWaitingEventListener, WaitingGroupFragment.FragmentWaitingGroupListener, CreateGroupFragment.CreateGroupFragmentListener, WaitingRestaurantFragment.FragmentWaitingRestaurantListener, SwipeTestFragment.FragmentSwipeListener, SwipingFragment.SwipingFragmentListener {


    public static final String TAG = "SignOutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super .onCreate(savedInstanceState);
        setContentView(R.layout.activity_signout);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WaitingEventFragment()).commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WaitingRestaurantFragment()).commit();


    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

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

    @Override
    public void onLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent toSignIn = new Intent(SignOutActivity.this, SignInActivity.class);
        startActivity(toSignIn);
        finish();
    }

    @Override
    public void onCreateEvent() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateEventFragment()).addToBackStack(null).commit();
    }





    @Override
    public void onListingRestaurant(int[] number, HashMap<String, Integer> listRestVotes, HashMap<String, HashMap<String, Object>> listRestInfo, ArrayList<String> restAddr, ArrayList<String> restName) {
        Fragment toSwipe = new SwipingFragment();
        Bundle bundle = new Bundle();
        bundle.putIntArray("num", number);
        bundle.putSerializable("votes", (Serializable) listRestVotes);
        bundle.putStringArrayList("name", restName);
        bundle.putStringArrayList("addr", restAddr);
        toSwipe.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, toSwipe).commit();
        Log.i(TAG, "received rest name: "+ restName.toString());
        Log.i(TAG, "received rest addr: " + restAddr.toString());
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
//        Intent toTest = new Intent(SignOutActivity.this, SwipeActivity.class);
//        startActivity(toTest);
    }

    @Override
    public void onCreateGroup() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateGroupFragment()).commit();
    }


    @Override
    public void onNewEventUpdate() {
        Log.i(TAG, "onNewEventUpdate()");
    }

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

    @Override
    public void onNewGroupUpdate(String groupId, Group grp) {
    }

    @Override
    public void selectRestaurant(String eventId, boolean firstEntry) {
        // Go to Retrieving Restaurant Fragment
        Fragment toRetrieveRest = new SwipingFragment();
        Bundle bundle = new Bundle();
        bundle.putString("eventId", eventId);
        bundle.putBoolean("firstEntry", firstEntry);
        toRetrieveRest.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, toRetrieveRest).commit();

//        Fragment toSwipe = new SwipingFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("eventId", eventId);
//        toSwipe.setArguments(bundle);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, toSwipe).commit();
////        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SwipeTestFragment()).commit();
    }

    @Override
    public void updateRest(String event_id, int number, HashMap<String, Integer> listRestVotes, boolean checkLastItem) {
        if (number < listRestVotes.size() - 1) {
//            Log.i(TAG, number.toString());
            Log.i(TAG, listRestVotes.toString());
            Fragment toSwipe = new SwipingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("eventId", event_id);
            bundle.putInt("num", number);
            bundle.putSerializable("votes", listRestVotes);
            bundle.putBoolean("checkLastItem", checkLastItem);
            toSwipe.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, toSwipe).commit();
            Log.i(TAG, "sending new swipe details");
//            Log.i(TAG, "numbr: " + number.toString());
            Log.i(TAG, "number: " + number);
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
//            Log.i(TAG, "numbr: " + number.toString());
            Log.i(TAG, "number: " + number);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WaitingEventFragment()).commit();
        }

    }


}
