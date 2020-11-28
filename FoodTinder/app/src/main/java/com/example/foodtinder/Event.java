package com.example.foodtinder;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Event {
    static DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    String eventId;
    DatabaseReference event_ref;
    //Understand needs of Events before implementing
}
