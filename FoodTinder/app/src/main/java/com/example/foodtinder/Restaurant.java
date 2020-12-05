package com.example.foodtinder;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Restaurant {
    //TODO create restaurant class
    static Integer count = 0;
    String name;
    String formatted_address;
    String rating;
    String business_status;
    String price_level;
    String location;
    ArrayList<String> images;
    
    static DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("RESTAURANT");
    
    Restaurant(){}
    
    Restaurant(String name, String formatted_address, String business_status, String rating, String price_level, String location, ArrayList<String> images){
        this.name=name;
        this.rating=rating;
        this.business_status=business_status;
        this.formatted_address=formatted_address;
        this.images=images;
        this.location=location;
        this.price_level=price_level;
        count++;
    }

    Restaurant(String name, HashMap<String, Object> info, ArrayList<String> images){
        this.name=name;
        this.rating=(String) info.get("rating");
        this.business_status=(String)info.get("business_status");
        this.formatted_address=(String) info.get("formatted_address");
        this.images=images;
        this.location=(String)info.get("location");
        this.price_level=(String) info.get("price_level");
        count++;
    }

    Restaurant(HashMap<String, Object> info){
        this.name=(String)info.get("name");
        this.rating=(String) info.get("rating");
        this.business_status=(String)info.get("business_status");
        this.formatted_address=(String) info.get("formatted_address");
        this.images = new ArrayList<>();
        this.images.addAll((ArrayList<String>) info.get("images"));
        this.location=(String)info.get("location");
        this.price_level=(String)info.get("price_level");
        count++;
    }

    public void createRestaurant(){
        ref.child(String.valueOf(count)).setValue(this);
    }

    static public void retrieveAllRestaurants(final DatabaseCallback dbcallback){
        final ArrayList<Restaurant> allInfo = new ArrayList<>();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<HashMap<String, Object>> gt = new GenericTypeIndicator<HashMap<String, Object>>() {};
                for(DataSnapshot item: snapshot.getChildren()){
                    HashMap<String, Object> temp = item.getValue(gt);
                    Restaurant r = new Restaurant(temp);
                    allInfo.add(r);
                }
                Log.i("Retrieve Restaurants", allInfo.toString());
                dbcallback.onCallback(allInfo, true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String getName(){return this.name;}
    public String getFormatted_address(){return this.formatted_address;}
    public String getBusiness_status(){return this.business_status;}
    public String getRating(){return this.rating;}
    public String getLocation(){return this.location;}
    public String getPrice_level(){return this.price_level;}
    public ArrayList<String> getImages(){return this.images;}
}
