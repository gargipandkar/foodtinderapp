package com.example.foodtinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class GroupLandingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grouplink);
        checkForDynamicLinks();

    }

    //Function to check for Grp Invitation Dynamic Links
    //Call this function in OnCreate/OnStart for the activity receiving the dynamic link intent
    private void checkForDynamicLinks(){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if (deepLink != null){
                            final String grpId = deepLink.getQueryParameter("grpId");
                            if (grpId != null) {
                                final Group joiningGroup = new Group(grpId);
                                AlertDialog.Builder builder = new AlertDialog.Builder(GroupLandingPage.this);
                                builder.setMessage("You have been invited to a group: " + joiningGroup.getName() + "\nDo you want to join the group?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //Add user to this group id's list
                                               // joiningGroup.addUser(user);//user should be a proper User object of the authenticated user.
                                                //Add intent to go to newly joined group page
                                                Intent toGroupPage = new Intent(GroupLandingPage.this, ListGroupsActivity.class);
                                                startActivity(toGroupPage);

                                            }
                                        })
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                builder.create().show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Invalid link.", Toast.LENGTH_LONG).show();
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