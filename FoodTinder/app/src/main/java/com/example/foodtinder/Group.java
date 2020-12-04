package com.example.foodtinder;

import android.net.Uri;
import android.provider.ContactsContract;
import android.util.AndroidRuntimeException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.ArrayList;
import java.util.HashMap;
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
    String link;

    Group(){}

    Group (Group group){
        this.id = group.id;
        this.name = group.name;
        this.memberCount = group.memberCount;
    }

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
        retrieveName();
    }

    public static void retrieveGroup (String id, final DatabaseCallback dbcallback){
        DatabaseReference grp_ref = db.child("GROUPS/" + id);
        grp_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group grp = snapshot.getValue(Group.class);
                dbcallback.onCallback(grp);
                Log.i("Check", grp.toString());
        }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    //function to call when user wants to create a brand new group
    //also returns a group object to be passed around
    public static Group createGroup(String name){
        //Use push() to let firebase help us create a unique id
        String newId = db.child("GROUPS").push().getKey();
        Group newGroup = new Group(newId);

        //Set group name and add creator to the userlist
        newGroup.ref.child("name").setValue(name);
        newGroup.addUser(newId);
        return newGroup;
    }

    void addUser(String grpId){
        //ADD USER TO GROUP AND GROUP TO USER
        this.ref = db.child("GROUPS").child(grpId);
        final DatabaseReference userList_ref = this.ref.child("listOfUsers").child(User.getId());
        //CHECK IF USER IS ALREADY IN GROUP
        userList_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    userList_ref.setValue(true);
                    User.addGroup(id);
                    ref.child("memberCount").setValue(memberCount+1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    /*void addUser(String grpId){
        //ADD USER TO GROUP AND GROUP TO USER
        this.ref = db.child("GROUPS").child(grpId);
        this.ref.child("listOfUsers").child(User.getId()).setValue(true);
        User.addGroup(grpId);
        this.ref.child("memberCount").setValue(this.memberCount+1);
    }

     */

    void addEvent(Event event){
        this.ref.child("listOfEvents").child(String.valueOf(event.id)).setValue(true);
        //event.ref.child("group").setValue(this.id);
    }

    void updateAllUsers(final String eventId, final String grpId){
        //GO TO EVERY USER IN THIS GROUP, GO TO THEIR LIST OF EVENTS AND ADD SENT IN EVENT
        ref = db.child("GROUPS").child(grpId);
        ref.child("listOfUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<HashMap<String, Boolean>> gt = new GenericTypeIndicator<HashMap<String, Boolean>>() {};
                HashMap<String, Boolean> allUsers = snapshot.getValue(gt);
                for(String user: allUsers.keySet()){
                    DatabaseReference u = db.child("USERS").child(user).child("listOfEvents");
                    u.child(eventId).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    String retrieveName(){
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

    String getShareableLink() {
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://foodtinder.example.com/?grpId="+this.id))
                .setDomainUriPrefix("https://foodtinder.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        String dynamicLinkString = dynamicLink.getUri().toString();
        this.link = dynamicLinkString;
        return dynamicLinkString;
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

    public String getId(){return this.id;}
    public String getName(){return this.name;}
    public String getCreator(){return this.creator;}
    public Integer getMemberCount(){ return this.memberCount; }
    
}
