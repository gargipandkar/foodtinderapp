package com.example.foodtinder;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Group {
    static DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    String groupId;
    DatabaseReference group_ref;

    //Used to pass around group reference across activities only
    Group(String groupId){
        this.groupId = groupId;
        this.group_ref = db.child("GROUPS/" + groupId);
    }

    //function to call when user wants to create a brand new group
    //also returns a group object to be passed around
    public static Group createGroup(String name, User creator){
        //Use push() to let firebase help us create a unique id
        DatabaseReference ref = db.child("GROUPS").push();
        String newId = ref.getKey();
        Group newGroup = new Group(newId);
        newGroup.setName(name);
        newGroup.addUser(creator);
        return newGroup;
    }

    void setName(String name){
        this.group_ref.child("Name").setValue(name);
    }

    void addUser(User user){
        this.group_ref.child("listOfUsers").child(user.userId).setValue(true);
        user.addGroup(this);
    }

    void addEvent(Event event){
        this.group_ref.child("listOfEvents").child(event.eventId).setValue(true);
    }

    String getName(){
        return group_ref.child("Name").getKey();
    }

    //this function will probably not be needed as we will no longer need to store counts for ID
    private static void addCountListener(){
        db.child("GROUPS").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Integer i = (Integer) snapshot.child("groupCount").getValue();
                db.child("GROUPS/groupCount").setValue(i+1);
                db.child("Test").setValue(true);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Integer i = (Integer) snapshot.child("groupCount").getValue();
                db.child("GROUPS/groupCount").setValue(i-1);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Group Class",error.toString());
            }
        });
    }
}
