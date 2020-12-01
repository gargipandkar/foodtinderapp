package com.example.foodtinder;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    Integer rating;
    String business_status;
    Integer price_level;
    String location;
    ArrayList<String> images;
    
    static DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("RESTAURANTS");
    
    Restaurant(){}
    
    Restaurant(String name, String formatted_address, String business_status, Integer rating, Integer price_level, String location, ArrayList<String> images){
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
        this.rating=(Integer) info.get("rating");
        this.business_status=(String)info.get(business_status);
        this.formatted_address=(String) info.get(formatted_address);
        this.images=images;
        this.location=(String)info.get(location);
        this.price_level=(Integer)info.get(price_level);
        count++;
    }

    public void createRestaurant(){
        ref.child(String.valueOf(count)).setValue(this);
    }

    static public ArrayList<Restaurant> retrieveAllRestaurants(){
        final ArrayList<Restaurant> allInfo = new ArrayList<>();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item: snapshot.getChildren())
                    allInfo.add((Restaurant) item.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return allInfo;
    }

    public String getName(){return this.name;}
    public String getFormattedAddress(){return this.formatted_address;}
    public String getBusinessStatus(){return this.business_status;}
    public Integer getRating(){return this.rating;}
    public String getLocation(){return this.location;}
    public Integer getPriceLevel(){return this.price_level;}
    public ArrayList<String> getImages(){return this.images;}
}
