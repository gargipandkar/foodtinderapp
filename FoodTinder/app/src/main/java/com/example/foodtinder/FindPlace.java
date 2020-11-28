package com.example.foodtinder;

import java.util.ArrayList;
import java.util.Arrays;

//FindPlace implementation of Places API
//Find Place require input text for query and inputtype (in this case we sue the default text)
// It also requires fields, which contains the list of values to return from the search (for now, it is fixed but can be customised later)_
//Returns the top result (one place only)  fulfilling the parameter passed

public class FindPlace {

    private String format = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?";
    private String input;
    private String inputtype;
    private ArrayList<String> fields = new ArrayList<>(Arrays.asList("name","formatted_address","types","photos","place_id","geometry"));

    //constructor method
    FindPlace(String input){
        this.input = input;
        this.inputtype = "textquery";
    }

    //generate url to be send to Response Class
    String generateURL(){
        String noSpaceInput = this.input.replaceAll("\\s+","+");
        String query="";
        for (String field:this.fields){
            query+=field+",";
        }
        query = query.substring(0,query.length()-1);
        return format + "input=" + noSpaceInput + "&inputtype=" + this.inputtype + "&fields=" + query + "&key=";
    }

}
