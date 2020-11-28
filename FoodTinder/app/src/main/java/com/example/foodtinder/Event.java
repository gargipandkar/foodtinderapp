package com.example.foodtinder;


import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class Event {

    String name;
    String group;
    String host;
    Calendar eventDateTime;
    Calendar prefDateTime;
    String location;
    String budget;
    String status;
    Boolean active;
    String decision;

    //preference related variables
    Event(String name, String group, String host, Calendar eventDateTime, String location, String budget, String prefDeadline, String eventStatus){
        this.name = group;
        this.group = group;
        this.host = host;
        this.eventDateTime = eventDateTime;
        setPrefDeadline(prefDeadline);
        this.location = location;
        this.budget = budget;
        updateEventStatus();
        checkExpiry();
        this.decision = "Undecided";
    }

    public void createEvent(){}

    public void setPrefDeadline(String prefDeadline){
        switch (prefDeadline){
            case "1 day before": this.prefDateTime = (Calendar)this.eventDateTime.clone();
                                    this.prefDateTime.add(Calendar.DATE, -1);
                                    break;

            case "1 week before": this.prefDateTime = (Calendar)this.eventDateTime.clone();
                                    this.prefDateTime.add(Calendar.WEEK_OF_MONTH, - 1);
                                    break;

            case "Today": this.prefDateTime = (Calendar)this.eventDateTime.clone();
                        this.prefDateTime.set(Calendar.HOUR_OF_DAY, 23);
                        this.prefDateTime.set(Calendar.MINUTE, 59);
                        break;

            default: this.prefDateTime = (Calendar)this.eventDateTime.clone();
        }
    }

    public void checkExpiry(){
        Calendar now = Calendar.getInstance();
        if (this.eventDateTime.after(now))
            active = false;
        active = true;
    }

    public void updateEventStatus(){
        Calendar now = Calendar.getInstance();
        if (this.prefDateTime.after(now))
            this.status = "Ready to swipe";
        status = "Waiting for preferences";
    }

    public String getName(){return this.name;}
    public String getGroup(){return this.group;}
    public String getHost(){return this.host;}
    public String getLocation(){return this.location;}
    public String getBudget(){return this.budget;}
    public String getStatus(){return this.status;}
    public String getDecision(){return this.decision;}
    public long getEventDateTime(){return this.eventDateTime.getTimeInMillis();}
    public long getPrefDateTime(){return this.prefDateTime.getTimeInMillis();}
    public Boolean getActive(){return this.active;}

    @Exclude
    public ArrayList<String> getDisplayDetails(){
        ArrayList<String> display = new ArrayList<>();
        display.add(this.name);
        display.add(this.group);
        display.add(this.eventDateTime.toString());
        display.add(this.status);
        if (!this.status.equals("Decided"))
            display.add(this.prefDateTime.toString());
        if (!this.decision.equals("Undecided"))
            display.add(this.decision);
        return display;
    }





}
