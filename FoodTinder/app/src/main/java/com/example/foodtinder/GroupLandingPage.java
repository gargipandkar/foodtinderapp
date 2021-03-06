//package com.example.foodtinder;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.DialogInterface;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
//import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
//
//import java.math.BigDecimal;
//import java.math.MathContext;
//import java.math.RoundingMode;
//
//public class GroupLandingPage extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_grouplink);
//        checkForDynamicLinks();
//
//    }
//
//    //Function to check for Grp Invitation Dynamic Links
//    //Call this function in OnCreate/OnStart for the activity receiving the dynamic link intent
//    private void checkForDynamicLinks(){
//        FirebaseDynamicLinks.getInstance()
//                .getDynamicLink(getIntent())
//                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
//                    @Override
//                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
//                        // Get deep link from result (may be null if no link is found)
//                        Uri deepLink = null;
//                        if (pendingDynamicLinkData != null) {
//                            deepLink = pendingDynamicLinkData.getLink();
//                        }
//
//                        if (deepLink != null){
//                            String grpId = deepLink.getQueryParameter("grpId");
//                            if (grpId != null) {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(GroupLandingPage.this);
//                                builder.setMessage("Join group? Grp Id: " + grpId)
//                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int id) {
//                                                //TODO Create group object with id
//                                                //TODO Add user to this group id's list
//                                                //TODO Add intent to go to ListGroupsActivity
//                                            }
//                                        })
//                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int id) {
//                                            }
//                                        });
//                                builder.create().show();
//                            }
//                            else{
//                                Toast.makeText(getApplicationContext(),"Invalid link.", Toast.LENGTH_LONG).show();
//                            }
//
//                        }
//
//                    }
//                })
//                .addOnFailureListener(GroupLandingPage.this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("GroupLandingPage", "getDynamicLink failure", e);
//                    }
//                });
//    }
//}

package com.example.foodtinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ActionMenuView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.example.foodtinder.Group;

import java.util.ArrayList;

public class GroupLandingPage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_group);

        boolean fromSignedIn = getIntent().getBooleanExtra("EXTRA_SIGNED_DL", false);
        boolean fromNewUser = getIntent().getBooleanExtra("EXTRA_NOT_SIGNEDIN_DL_SIGNED", false);

        // If user is not signed in or the app is not in the background, redirect user to sign in
        // Else dynamic link will be shown and user can choose to be added into the group
        if (User.getId() == null && fromSignedIn == false && fromNewUser == false) {
            Intent directToSignIn = new Intent (GroupLandingPage.this, MainActivity.class);
            directToSignIn.putExtra("EXTRA_FROM_DL", true);
            checkForDynamicLinks(false);
            startActivity(directToSignIn);
        } else {
            checkForDynamicLinks(true);
        }

    }

    // Function to check for Group invitation via Dynamic Link
    // Call this function in OnCreate/OnStart for the activity receiving the dynamic link intent
    private void checkForDynamicLinks(Boolean isSignIn){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;

                        if (isSignIn == true){
                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.getLink();
                            }

                            if (deepLink != null){
                                final String grpId = deepLink.getQueryParameter("grpId");
                                Log.i("Check", grpId);
                                if (grpId != null) {
                                    Group.retrieveGroup(grpId, new DatabaseCallback() {
                                        @Override
                                        public void onCallback(ArrayList<String> ls) { }
                                        @Override
                                        public void onCallback(Event event) { }
                                        @Override
                                        public void onCallback(ArrayList<Restaurant> allRest, boolean done){}

                                        @Override
                                        public void onCallback(Group grp) {
                                            final Group joiningGroup = new Group(grp);
                                            Log.i("Check", joiningGroup.toString());

                                            AlertDialog.Builder builder = new AlertDialog.Builder(GroupLandingPage.this);
                                            builder.setMessage("You have been invited to a group: \n" + joiningGroup.getName() + "\n" + "\nDo you want to join the group?")
                                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            joiningGroup.addUser(grpId);
                                                            // Reload the app to home page
                                                            Intent toGroupPage = new Intent(GroupLandingPage.this, MainActivity.class);
                                                            toGroupPage.putExtra("EXTRA_JOINED_GROUP", true);
                                                            startActivity(toGroupPage);
                                                        }
                                                    })
                                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // Reload the app to home page
                                                            Intent toHomePage = new Intent(GroupLandingPage.this, MainActivity.class);
                                                            startActivity(toHomePage);
                                                        }
                                                    });
                                            builder.create().show();
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Invalid link.", Toast.LENGTH_LONG).show();
                                }

                            }
                        }



                    }
                })
                .addOnFailureListener(GroupLandingPage.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("GroupPage", "getDynamicLink failure", e);
                    }
                });
    }
}