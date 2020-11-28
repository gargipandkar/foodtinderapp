package com.example.foodtinder;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User {
    static DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    String userId;
    DatabaseReference user_ref;

    User(String userId){
        this.userId = userId;
        this.user_ref = db.child("USERS/"+userId);
    }

    //this is a test implementation. change once you know where to get userId/email whatever
    public static User createUser(String userId, String name){
        db.child("USERS/"+userId+"/Name").setValue(name);
        return new User(userId);
    }

    void addGroup(Group group){
        user_ref.child("listOfGroups").child(group.groupId).setValue(true);
    }
}
