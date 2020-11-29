package com.example.foodtinder;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//Main class to search at most 20 restaurants in the vicinity of a coordinate point (longitude and latitude)
//Produce a HashMap of Name and Attributes of places that fit the desired specifications as well as their locations
public class Main {

    public static void main() throws JSONException {
        Processor processor = new Processor(
                "Tampines",
                1,
                4,
                "bar",
                10000); //type can be cafe / bar / restaurant
        processor.processQueries();
        Log.w("Check", processor.finalResult.toString());
        Log.w("Check", processor.finalResultPhotos.toString());
        //System.out.println(processor.finalResult);
        //System.out.println(processor.finalResultPhotos);

    }
}
