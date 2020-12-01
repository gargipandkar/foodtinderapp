//
//package com.example.foodtinder;
//
//import android.provider.ContactsContract;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Group {
//    String name;
//    Integer memberCount;
//    ArrayList<String> membersList;
//    ArrayList<String> eventsList;
//
//    Group(){}
//
//    Group(String name, DatabaseReference members, DatabaseReference events){
//        this.name = name;
//        this.eventsList = getGroupMembers(members);
//        this.memberCount = getMemberCount();
//        this.eventsList = getGroupEvents(events);
//    }
//
//    private Integer getMemberCount(){
//        return membersList.size();
//    }
//
//    public ArrayList<String> getGroupMembers(DatabaseReference groupMembers_ref){
//        final ArrayList<String> ls = new ArrayList<>();
//        groupMembers_ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot member: snapshot.getChildren())
//                    ls.add(member.getKey());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w("Check", error.toException());
//            }
//        });
//        return ls;
//    }
//
//    public ArrayList<String> getGroupEvents (DatabaseReference groupEvents_ref){
//        final ArrayList<String> ls = new ArrayList<>();
//        groupEvents_ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot event: snapshot.getChildren())
//                    ls.add(event.getKey());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w("Check", error.toException());
//            }
//        });
//        return ls;
//    }
//
//}

package com.example.foodtinder;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Group {
    static DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    String id;
    String name;
    String creator;
    Integer memberCount = 0;
    ArrayList<String> membersList;
    ArrayList<String> eventsList;
    DatabaseReference ref;

    Group(){}

    Group(String id, String name, String creator, DatabaseReference members, DatabaseReference events){
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.membersList = getGroupMembers(members);
        this.memberCount = getMemberCount();
        this.eventsList = getGroupEvents(events);
        this.ref = db.child("GROUPS").child(this.id);
    }

    //Used to pass around group reference across activities only
    Group(String id){
        this.id = id;
        this.ref = db.child("GROUPS/" + id);
        //update the local object to have latest values since its async
        getName();
    }

    //function to call when user wants to create a brand new group
    //also returns a group object to be passed around
    public static Group createGroup(String name){
        //Use push() to let firebase help us create a unique id
        String newId = db.child("GROUPS").push().getKey();
        Group newGroup = new Group(newId);

        //Set group name and add creator to the userlist
        newGroup.ref.child("name").setValue(name);
        newGroup.addUser();
        return newGroup;
    }

    void addUser(){
        //ADD USER TO GROUP AND GROUP TO USER
        this.ref.child("listOfUsers").child(User.getId()).setValue(true);
        User.addGroup(this.id);
        this.ref.child("memberCount").setValue(this.memberCount+1);
    }

    void addEvent(Event event){
        this.ref.child("listOfEvents").child(String.valueOf(event.id)).setValue(true);
        //event.ref.child("group").setValue(this.id);
    }


    String getName(){
        if (name == null){
            ref.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    name = snapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Group class", error.toString());
                }
            });
        }
        return this.name;
    }

    String getShareableLink() {return "link_here";}

    private Integer getMemberCount(){
        return membersList.size();
    }

    public ArrayList<String> getGroupMembers(DatabaseReference groupMembers_ref){
        final ArrayList<String> ls = new ArrayList<>();
        groupMembers_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot member: snapshot.getChildren())
                    ls.add(member.getKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Check", error.toException());
            }
        });
        return ls;
    }

    public ArrayList<String> getGroupEvents (DatabaseReference groupEvents_ref){
        final ArrayList<String> ls = new ArrayList<>();
        groupEvents_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot event: snapshot.getChildren())
                    ls.add(event.getKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Check", error.toException());
            }
        });
        return ls;
    }

}