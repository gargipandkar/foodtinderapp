package com.example.placeslib;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class Processor {

    String placeQuery;
    Integer minPriceQuery;
    Integer maxPriceQuery;
    String type;
    Integer radius;
    HashMap<String, HashMap<String, String>> finalResult = new HashMap<>();
    HashMap<String, ArrayList<String>> finalResultPhotos = new HashMap<>();

    Processor(String placeQuery, Integer minPriceQuery, Integer maxPriceQuery, String type, Integer radius){
        this.placeQuery = placeQuery;
        this.minPriceQuery = minPriceQuery;
        this.maxPriceQuery = maxPriceQuery;
        this.type = type;
        this.radius = radius;
    }

    void processQueries() throws JSONException {
        //key from Google Places API (do not edit or share with other people)
        String API_KEY = "AIzaSyD72OjefU10z49J3qCD6Cs2iXHi8uvVBjo";

        // instantiate Response Object with the Key to send query to https
        Response response = new Response(API_KEY);
        response.setKey(API_KEY);

        //instantiate Result Object with to manage all the results
        Result result = new Result();

        //instantiate FP object, generate URL and obtain response
        FindPlace FP = new FindPlace(this.placeQuery);
        response.setUrl(FP.generateURL());
        String responseFP = response.generateResponse();

        //parse the results
        result.findPlaceParser(responseFP);

        //instantiate NearbySearch Object to begin search of places around a specified longitude and latitude, with other parameters
        NearbySearch NS = new NearbySearch((double)result.placeInfo.get("longitude"), (double)result.placeInfo.get("latitude"), this.radius,this.minPriceQuery, this.maxPriceQuery, this.type);

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

        //instantiate new PhotoReference object to store the photo reference values and generate them from the placeDetails result
        PhotoReference pR = new PhotoReference();
        pR.generateReference(result);

        //An example of how to generate all url for obtaining the image, refer to the PhotoReference class for details
        // Looping through all locations and then loop through each of its index
        //Store in ArrayList or Hash Map according to needs of application
        for (String name:pR.photoRef.keySet()){
            for (int i=0; i<pR.photoRef.get(name).size();i++) {
                response.setUrl(pR.generateURL(name, i));

                ArrayList<String> photoRef = finalResultPhotos.get(name);

                if (photoRef==null){
                    photoRef = new ArrayList<String>();
                }
                photoRef.add(response.urlStr);



                finalResultPhotos.put(name,photoRef);


            //System.out.println(response.urlStr); //second result to generate url for photos of results
            }
        }
        finalResult = result.placeDetails;

    }
}
