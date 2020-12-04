package com.example.foodtinder;
import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class fetchData extends AsyncTask<Void,Void,Void> {
    private BufferedReader reader;
    private String line;
    private StringBuffer responseContent = new StringBuffer();
    private  HttpURLConnection connection;
    private String toParse;
    private String urlStr;
    private String type;

    public static HashMap<String, String> result;
    public static ArrayList<String> placeIDs;
    DatabaseReference db;

    void setType(String type){
        this.type = type;
    }

    void setUrl(String url){
        this.urlStr = url;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            responseContent.delete(0, responseContent.length());
            URL url = new URL(this.urlStr);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();
            if (status>299){
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
                reader.close();
            }
            else{
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null){

                    responseContent.append(line);
                }
                reader.close();
            }
            toParse = '[' + responseContent.toString() + ']';
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            connection.disconnect();
        };
        return null;

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Parser parser = Parser.getParser();
        URLGenerator urlGenerator = new URLGenerator();
        switch(type){
            case "findplace":
                try {
                    parser.findPlaceParser(toParse);
                    result = parser.queryInfo;

                    fetchData process = new fetchData();
                    process.setType("nearbysearch");

                    urlStr = urlGenerator.generateNearbySearchURL(parser.queryInfo);
                    process.setUrl(urlStr);
                    process.execute();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "nearbysearch":
                try {
                    parser.nearbySearchParser(toParse);
                    placeIDs = parser.placeIDs;

                    fetchData process = new fetchData();
                    process.setType("placedetails");
                    if (placeIDs.size()>0) {
                        urlStr = urlGenerator.generatePlaceDetailsURL(placeIDs.remove(0));
                        process.setUrl(urlStr);
                        process.execute();
                    }
                    else{
                        ;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "placedetails":
                try {
                    parser.placeDetailsParser(toParse);
                    fetchData process = new fetchData();
                    process.setType("placedetails");
                    if (placeIDs.size()>0){
                        urlStr = urlGenerator.generatePlaceDetailsURL(placeIDs.remove(0));
                        process.setUrl(urlStr);
                        process.execute();
                    }
                    else{
                        db = FirebaseDatabase.getInstance().getReference().child("EVENTS/"+parser.eventId);
                        db.child("placeDetails").setValue(parser.placeDetails);
                        db.child("placeDetailsPhotos").setValue(parser.placeDetailsPhotos);
                        db.child("status").setValue("Ready to swipe");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
