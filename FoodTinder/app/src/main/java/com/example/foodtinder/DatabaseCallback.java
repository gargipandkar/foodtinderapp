package com.example.foodtinder;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public interface DatabaseCallback {
    void onCallback(ArrayList<String> ls);

    void onCallback(Event event);

    void onCallback(Group grp);

    void onCallback(ArrayList<Restaurant> allRest, boolean done);
}