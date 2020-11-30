
package com.example.foodtinder;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Group {
    String name;
    Integer memberCount;
    ArrayList<String> membersList;
    ArrayList<String> eventsList;

    Group(){}

    Group(String name, DatabaseReference members, DatabaseReference events){
        this.name = name;
        this.eventsList = getGroupMembers(members);
        this.memberCount = getMemberCount();
        this.eventsList = getGroupEvents(events);
    }

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