package com.example.foodtinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

//PhotoReference implementation of Places API
//Photo Reference require photo_reference value, obtained from PlaceDetails or NearbySearch (in this case, we are using PlaceDetails)
//It also takes a maxwidth/maxheight value (either one), which is going to be used to scale the image (smaller value is picked)
//There may be multiple photo_reference value, each stored in an ArrayList.
//Returns an image of of a size constrained by maxwidth/maxheight (set to 1600 to ensure image original size is returned)
//The url from this class can be passed to ImageView directly to display the image

public class PhotoReference {

    private String format = "https://maps.googleapis.com/maps/api/place/photo?";
    HashMap<String, ArrayList<String>> photoRef = new HashMap<>();
    private int maxWidth=1600;
    private int maxHeight=1600;

    PhotoReference(){};

    //Method to obtain the HashMap of Names and an ArrayList of all its photo_reference from the Result class's placeDetails variable
    void generateReference(Result result) throws JSONException {
        for (String place:result.placeDetailsPhotos.keySet()){

            if (result.placeDetailsPhotos.get(place).get("photos") == null){
                this.addReference(place,null);
            } else {

                JSONArray res = new JSONArray(result.placeDetailsPhotos.get(place).get("photos").toString());

                for (int i=0;i<res.length();i++) {
                    JSONObject resObj = res.getJSONObject(i);
                    this.addReference(place, resObj.getString("photo_reference"));
                }

            }
        }
    }

    void setMaxWidth(int maxWidth){
        this.maxWidth = maxWidth;
    }

    void setMaxHeight(int maxHeight){
        this.maxHeight = maxHeight;
    }

    int getMaxWidth(int maxWidth){
        return this.maxWidth;
    }

    int getMaxHeight(int maxHeight){
        return this.maxHeight;
    }


    //Method to add a new reference value to a location
    void addReference(String name, String pR){
        ArrayList<String> photoRefList = photoRef.get(name);
        if (photoRefList == null){
            photoRefList = new ArrayList<String>();
        }
        photoRefList.add(pR);
        photoRef.put(name, photoRefList);
    }

    //generate url to be send to Response Class
    //Receive a location name and index to choose which reference value is picked from its ArrayList of reference values
    String generateURL(String name, int index){
        String reference = photoRef.get(name).get(index);
        String query = "maxwidth=" + this.maxWidth + "&maxheight"+ this.maxHeight + "&photoreference="+reference;
        return format + query + "&key=";
    }


}
