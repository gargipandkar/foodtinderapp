package com.example.foodtinder;

import android.content.DialogInterface;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    User user;
    Group testgrp;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = User.createUser("testUser", "Ricargo");
        testgrp = Group.createGroup("TESTTTTT", user);

        Button enter_btn = findViewById(R.id.enter_btn);
        enter_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Log.i("Check", "Button clicked");
//                Intent toCreateEvent = new Intent(MainActivity.this, CreateEventActivity.class);
//                startActivity(toCreateEvent);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String t = testgrp.getShareableLink();
                sendIntent.putExtra(Intent.EXTRA_TEXT, t);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        Button grp_btn = findViewById(R.id.grp_button);
        grp_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent toGroupPage = new Intent(MainActivity.this, GroupPage.class);
//                startActivity(toGroupPage);

                String text = testgrp.getName();
                Toast.makeText(getApplicationContext(),text, Toast.LENGTH_LONG).show();

            }
        });
    }
}
