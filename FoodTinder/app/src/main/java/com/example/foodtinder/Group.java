package com.example.foodtinder;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
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

// Group class to create and access Group object to update Firebase Realtime Database
// Implements parcelable to send objects between fragments
public class Group implements Parcelable {

    // TAG for debugging purposes
    public static final String TAG = "Group";

    // Initialise Firebase Realtime Database
    static DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    String id, name, creator, link;
    int memberCount = 0;
    ArrayList<String> membersList = new ArrayList<>();
    ArrayList<String> eventsList;
    DatabaseReference ref;


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

    // Used to pass around group reference across fragments in host Acitivity (SignOutActivity.java)
    Group(String id){
        this.id = id;
        this.ref = db.child("GROUPS/" + id);
        // Update the local object to have latest values since its async
        retrieveName();
    }

    protected Group(Parcel in) {
        id = in.readString();
        name = in.readString();
        creator = in.readString();
        memberCount = in.readInt();
        membersList = in.createStringArrayList();
        eventsList = in.createStringArrayList();
        link = in.readString();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    public static void retrieveGroup (String id, final DatabaseCallback dbcallback){
        DatabaseReference grp_ref = db.child("GROUPS/" + id);
        grp_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group grp = snapshot.getValue(Group.class);
                dbcallback.onCallback(grp);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // Function to call when user wants to create a brand new group
    // Also returns a group object to be passed around
    public static Group createGroup(String name){
        // Use push() to let firebase help us create a unique id
        String newId = db.child("GROUPS").push().getKey();
        Group newGroup = new Group(newId);

        // Set group name and add creator to the userlist
        newGroup.ref.child("name").setValue(name);
        newGroup.addUser();
        return newGroup;
    }

    // Function to call when user creates a new group
    // Add user to group and group to user in Firebase Realtime Database
    void addUser(){
        this.ref = db.child("GROUPS").child(this.id);
        this.ref.child("listOfUsers").child(User.getId()).setValue(true);
        User.addGroup(this.id);
        this.ref.child("memberCount").setValue(this.memberCount+1);
    }

    // Function to call when user joins a group
    // Add user to group and group to user in Firebase Realtime Database
    void addUser(String grpId){
        //ADD USER TO GROUP AND GROUP TO USER
        this.ref = db.child("GROUPS").child(grpId);
        this.ref.child("listOfUsers").child(User.getId()).setValue(true);
        User.addGroup(grpId);
        this.ref.child("memberCount").setValue(this.memberCount+1);
    }

    void addEvent(Event event){
        this.ref.child("listOfEvents").child(String.valueOf(event.id)).setValue(true);
    }

    // Function to call when user creates a new event
    // Add this event to every user in the same group in Firebase Realtime Database
    // Automatically updates list of events in every user's app
    void updateAllUsers(final String eventId, final String grpId){
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

    // Function to call to create a dynamic link when user creates a new group
    // Uses Group Id to create the dynamic link
    String getShareableLink(String grpId) {
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
        ref = db.child("GROUPS").child(grpId);
        ref.child("link").setValue(dynamicLinkString);
        return dynamicLinkString;
    }


    // Function to call to get the list of members in a group
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

    // Function to call to get the list of events created by that group
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
    public int getMemberCount(){return this.memberCount;}
    public String getLink(){return this.link;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(creator);
        parcel.writeInt(memberCount);
        parcel.writeStringList(membersList);
        parcel.writeStringList(eventsList);
        parcel.writeString(link);
    }
}

