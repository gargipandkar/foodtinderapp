package com.example.foodtinder;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Group {
    static DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    String groupId;
    DatabaseReference group_ref;
    String name = null;
    ArrayList<Event> listOfEvents = new ArrayList<Event>();
    ArrayList<User> listOfUsers = new ArrayList<User>();

    //Used to pass around group reference across activities only
    Group(String groupId){
        this.groupId = groupId;
        group_ref = db.child("GROUPS/" + groupId);
        //update the local object to have latest values since its async
        getName();
        getListOfUsers();
        getListOfEvents();
    }

    //function to call when user wants to create a brand new group
    //also returns a group object to be passed around
    public static Group createGroup(String name, User creator){
        //Use push() to let firebase help us create a unique id
        String newId = db.child("GROUPS").push().getKey();
        Group newGroup = new Group(newId);

        //Set group name and add creator to the userlist
        newGroup.group_ref.child("Name").setValue(name);
        newGroup.addUser(creator);
        return newGroup;
    }

    void addUser(User user){
        this.group_ref.child("listOfUsers").child(user.userId).setValue(true);
        user.addGroup(this);
    }

    void addEvent(Event event){
        this.group_ref.child("listOfEvents").child(event.eventId).setValue(true);
        event.event_ref.child("Group").setValue(this.groupId);
    }

    String getName(){
        if (name == null){
            group_ref.child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
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

    ArrayList getListOfEvents(){
        if (listOfEvents.size() == 0){
            group_ref.child("listOfEvents").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot eventSnapshot:snapshot.getChildren()){
                        listOfEvents.add(new Event(eventSnapshot.getKey()));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Group class", error.toString());
                }
            });
        }
        return this.listOfEvents;
    }

    ArrayList getListOfUsers(){
        if (listOfUsers.size() == 0){
            group_ref.child("listOfUsers").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot userSnapshot:snapshot.getChildren()){
                        listOfUsers.add(new User(userSnapshot.getKey()));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Group class", error.toString());
                }
            });
        }
        return this.listOfUsers;
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
