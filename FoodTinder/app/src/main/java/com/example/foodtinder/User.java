package com.example.foodtinder;

public class User {
    private String name, email;

    User(String name, String email, String photoString){
        this.name = name;
        this.email = email;
//        this.photoString = photoString;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }



}
