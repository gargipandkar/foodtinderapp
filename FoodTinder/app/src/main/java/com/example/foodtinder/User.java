//package com.example.foodtinder;
//
//public class User {
//    private String name, email;
//
//    User(String name, String email, String photoString){
//        this.name = name;
//        this.email = email;
////        this.photoString = photoString;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//
//
//}




package com.example.foodtinder;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.Future;

public class User  {
    public static String id;
    public static String name;
    public static String email;

    static ArrayList<String> activeEvents = new ArrayList<>();
    static ArrayList<String> inGroups = new ArrayList<>();

    User(){}

    User(String in_id, String in_name, String in_email){
        id = in_id;
        name = in_name;
        email = in_email;
    }

//    @Exclude
    public static String getId(){ return id;}
    public static String getName(){return name;}
    public static String getEmail(){return email;}

    public static void addGroup(String groupId){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = db.child("USERS").child(id);

        ref.child("inGroups").child(groupId).setValue(true);
    }

    public static void setUserGroups(DatabaseReference userGroups_ref, final DatabaseCallback dbcallback){
        final ArrayList<String> ls = new ArrayList<>();
        userGroups_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot group: snapshot.getChildren())
                    ls.add(group.getKey());
                dbcallback.onCallback(ls);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Check", error.toException());
            }
        });
    }

    public static void setUserEvents (DatabaseReference userEvents_ref, final DatabaseCallback dbcallback){
        final ArrayList<String> ls = new ArrayList<>();
        userEvents_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot event: snapshot.getChildren())
                    ls.add(event.getKey());
                dbcallback.onCallback(ls);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Check", error.toException());
            }
        });
    }
}