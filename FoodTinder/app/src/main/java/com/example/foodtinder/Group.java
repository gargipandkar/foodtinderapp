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

//package com.example.foodtinder;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//import android.provider.ContactsContract;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Group implements Parcelable {
//    static DatabaseReference db = FirebaseDatabase.getInstance().getReference();
//
//    String id;
//    String name;
//    String creator;
//    Integer memberCount = 0;
//    ArrayList<String> membersList;
//    ArrayList<String> eventsList;
//    DatabaseReference ref;
//
//    Group(){}
//
//    Group(String id, String name, String creator, DatabaseReference members, DatabaseReference events){
//        this.id = id;
//        this.name = name;
//        this.creator = creator;
//        this.membersList = getGroupMembers(members);
//        this.memberCount = getMemberCount();
//        this.eventsList = getGroupEvents(events);
//        this.ref = db.child("GROUPS").child(this.id);
//    }
//
//    //Used to pass around group reference across activities only
//    Group(String id){
//        this.id = id;
//        this.ref = db.child("GROUPS/" + id);
//        //update the local object to have latest values since its async
//        getName();
//    }
//
//    protected Group(Parcel in) {
//        id = in.readString();
//        name = in.readString();
//        creator = in.readString();
//        if (in.readByte() == 0) {
//            memberCount = null;
//        } else {
//            memberCount = in.readInt();
//        }
//        membersList = in.createStringArrayList();
//        eventsList = in.createStringArrayList();
//    }
//
//    public static final Creator<Group> CREATOR = new Creator<Group>() {
//        @Override
//        public Group createFromParcel(Parcel in) {
//            return new Group(in);
//        }
//
//        @Override
//        public Group[] newArray(int size) {
//            return new Group[size];
//        }
//    };
//
//    //function to call when user wants to create a brand new group
//    //also returns a group object to be passed around
//    public static Group createGroup(String name){
//        //Use push() to let firebase help us create a unique id
//        String newId = db.child("GROUPS").push().getKey();
//        Group newGroup = new Group(newId);
//
//        //Set group name and add creator to the userlist
//        newGroup.ref.child("name").setValue(name);
//        newGroup.addUser();
//        return newGroup;
//    }
//
//    void addUser(){
//        //ADD USER TO GROUP AND GROUP TO USER
//        this.ref.child("listOfUsers").child(User.getId()).setValue(true);
//        User.addGroup(this.id);
//        this.ref.child("memberCount").setValue(this.memberCount+1);
//    }
//
//    void addEvent(Event event){
//        this.ref.child("listOfEvents").child(String.valueOf(event.id)).setValue(true);
//        //event.ref.child("group").setValue(this.id);
//    }
//
//
//    String getName(){
//        if (name == null){
//            ref.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    name = snapshot.getValue(String.class);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Log.e("Group class", error.toString());
//                }
//            });
//        }
//        return this.name;
//    }
//
//    String getShareableLink() {return "link_here";}
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
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(id);
//        parcel.writeString(name);
//        parcel.writeString(creator);
//        if (memberCount == null) {
//            parcel.writeByte((byte) 0);
//        } else {
//            parcel.writeByte((byte) 1);
//            parcel.writeInt(memberCount);
//        }
//        parcel.writeStringList(membersList);
//        parcel.writeStringList(eventsList);
//    }
//}



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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.ArrayList;
import java.util.List;

public class Group implements Parcelable {
    static DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    String id;
    String name;
    String creator;
    int memberCount = 0;
    ArrayList<String> membersList = new ArrayList<>();
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
        newGroup.addUser();
        return newGroup;
    }

    void addUser(){
        //ADD USER TO GROUP AND GROUP TO USER
        this.ref = db.child("GROUPS").child(this.id);
        this.ref.child("listOfUsers").child(User.getId()).setValue(true);
        User.addGroup(this.id);
        this.ref.child("memberCount").setValue(this.memberCount+1);
    }

    void addUser(String grpId){
        //ADD USER TO GROUP AND GROUP TO USER
        this.ref = db.child("GROUPS").child(grpId);
        this.ref.child("listOfUsers").child(User.getId()).setValue(true);
        User.addGroup(grpId);
        this.ref.child("memberCount").setValue(this.memberCount+1);
    }

    void addEvent(Event event){
        this.ref.child("listOfEvents").child(String.valueOf(event.id)).setValue(true);
        //event.ref.child("group").setValue(this.id);
    }

    void updateAllUsers(final String eventId){
        //GO TO EVERY USER IN THIS GROUP, GO TO THEIR LIST OF EVENTS AND ADD SENT IN EVENT
        ref.child("listOfMembers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> allUsers = (ArrayList<String>) snapshot.getValue(ArrayList.class);
                for(String user: allUsers){
                    DatabaseReference u = db.child("USERS").child(user).child("activeEvents");
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
    public int getMemberCount(){
        if (membersList.isEmpty() == true) {
            return 0;
        } else {
            return membersList.size();
        }
    }

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