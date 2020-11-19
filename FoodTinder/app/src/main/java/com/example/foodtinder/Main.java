package com.example.lib;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

//Main class to search at most 20 restaurants in the vicinity of a coordinate point (longitude and latitude)
//Produce a HashMap of Name and Attributes of places that fit the desired specifications as well as their locations
public class Main {

    public static void main(String[] args){
        //key from Google Places API (do not edit or share with other people)
        String API_KEY = "AIzaSyD72OjefU10z49J3qCD6Cs2iXHi8uvVBjo";

        // instantiate Response Object with the Key to send query to https
        Response response = new Response(API_KEY);
        response.setKey(API_KEY);

        //instantiate Result Object with to manage all the results
        Result result = new Result();

        //instantiate FP object, generate URL and obtain response
        FindPlace FP = new FindPlace("Tampines");
        response.setUrl(FP.generateURL());
        String responseFP = response.generateResponse();

        //parse the results
        result.findPlaceParser(responseFP);

        //instantiate NearbySearch Object to begin search of places around a specified longitude and latitude, with other parameters
        NearbySearch NS = new NearbySearch((double)result.placeInfo.get("longitude"), (double)result.placeInfo.get("latitude"), 1000,0, 4, "restaurant");

        //get url for Nearby Search request
        response.setUrl(NS.generateURL());

        //store result from response object
        String responseNS = response.generateResponse();

        //parse the result from nearby search
        result.nearbySearchParser(responseNS);

        //instantiate PlaceDetails Object to begin search of attributes of a specific place based on its place_id
        PlaceDetails pD = new PlaceDetails(result.placeIDs);

        //generate list of urls for PlaceDetails search
        pD.generateURL();

        //for loop to store result of each PlaceDetails request
        for(String url:pD.urlList){
            response.setUrl(url);
            String responsePD = response.generateResponse();
            result.placeDetailsParser(responsePD);
        }


        //first result containing all the result from the search
        System.out.println(result.placeDetails);

        //instantiate new PhotoReference object to store the photo reference values and generate them from the placeDetails result
        PhotoReference pR = new PhotoReference();
        pR.generateReference(result);

        //An example of how to generate all url for obtaining the image, refer to the PhotoReference class for details
        // Looping through all locations and then loop through each of its index
        //Store in ArrayList or Hash Map according to needs of application
        for (String name:pR.photoRef.keySet()){
            System.out.println(name);
            for (int i=0; i<pR.photoRef.get(name).size();i++) {
                response.setUrl(pR.generateURL(name, i));
                System.out.println(response.urlStr); //second result to generate url for photos of results
            }
        }





    }
}
