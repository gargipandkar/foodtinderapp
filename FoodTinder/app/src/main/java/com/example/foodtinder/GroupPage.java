package com.example.foodtinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

public class GroupPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                            String grpId = deepLink.getQueryParameter("grpId");
                            if (grpId != null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(GroupPage.this);
                                builder.setMessage("Join group? Grp Id: " + grpId)
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //Create group object with id
                                                //Add user to this group id's list
                                                //Add intent to go to grp page
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
                .addOnFailureListener(GroupPage.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("GroupPage", "getDynamicLink failure", e);
                    }
                });
    }
}