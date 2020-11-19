package com.example.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//Response Object to request results using Http connection

public class Response {

    private String API_KEY;
    String urlStr;
    private BufferedReader reader;
    private String line;
    private StringBuffer responseContent = new StringBuffer();
    private  HttpURLConnection connection;
    private String toParse;

    //constructor method to create a new instance of the Response class
    Response(){};
    Response(String key){this.API_KEY = key;}

    //method to set url for Https request
    void setUrl(String urlStr){
        this.urlStr = urlStr + API_KEY;
    }

    //method to setKey to be passed to generateResponse
    void setKey(String key){
        this.API_KEY = key;
    }

    //method to request results from Http and return a String to be parsed using Json (don't edit)
    String generateResponse(){
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
        return toParse;
    }
}
