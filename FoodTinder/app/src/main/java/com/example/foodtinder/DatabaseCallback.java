package com.example.foodtinder;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public interface DatabaseCallback {
    void onCallback(ArrayList<String> ls);
}