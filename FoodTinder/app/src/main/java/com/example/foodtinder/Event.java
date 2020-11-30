package com.example.foodtinder;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class Event implements Parcelable {

    String name, group, host, location, budget, status;
    long eventDateTime;
//    Calendar prefDateTime;
//    Boolean active;
    String decision;

    //preference related variables
//    Event(String name, String group, String host, Calendar eventDateTime, String location, String budget, String prefDeadline, String eventStatus){
//        this.name = group;
//        this.group = group;
//        this.host = host;
//        this.eventDateTime = eventDateTime;
//        setPrefDeadline(prefDeadline);
//        this.location = location;
//        this.budget = budget;
//        updateEventStatus();
//        checkExpiry();
//        this.decision = "Undecided";
//    }

    Event(String name, String group, String host, Long eventDateTime, String location, String budget, String eventStatus){
        this.name = group;
        this.group = group;
        this.host = host;
        this.eventDateTime = eventDateTime;
        this.location = location;
        this.budget = budget;
        this.status = eventStatus;
        this.decision = "Undecided";
    }

    protected Event(Parcel in) {
        name = in.readString();
        group = in.readString();
        host = in.readString();
        location = in.readString();
        budget = in.readString();
        status = in.readString();
//        byte tmpActive = in.readByte();
//        active = tmpActive == 0 ? null : tmpActive == 1;
//        decision = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public void createEvent(){}

//    public void setPrefDeadline(String prefDeadline){
//        switch (prefDeadline){
//            case "1 day before": this.prefDateTime = (Calendar)this.eventDateTime.clone();
//                this.prefDateTime.add(Calendar.DATE, -1);
//                break;
//
//            case "1 week before": this.prefDateTime = (Calendar)this.eventDateTime.clone();
//                this.prefDateTime.add(Calendar.WEEK_OF_MONTH, - 1);
//                break;
//
//            case "Today": this.prefDateTime = (Calendar)this.eventDateTime.clone();
//                this.prefDateTime.set(Calendar.HOUR_OF_DAY, 23);
//                this.prefDateTime.set(Calendar.MINUTE, 59);
//                break;
//
//            default: this.prefDateTime = (Calendar)this.eventDateTime.clone();
//        }
//    }
//
//    public void checkExpiry(){
//        Calendar now = Calendar.getInstance();
//        if (this.eventDateTime.after(now))
//            active = false;
//        active = true;
//    }
//
//    public void updateEventStatus(){
//        Calendar now = Calendar.getInstance();
//        if (this.prefDateTime.after(now))
//            this.status = "Ready to swipe";
//        status = "Waiting for preferences";
//    }

    public String getName(){return this.name;}
    public String getGroup(){return this.group;}
    public String getHost(){return this.host;}
    public String getLocation(){return this.location;}
    public String getBudget(){return this.budget;}
    public String getStatus(){return this.status;}
    public String getDecision(){return this.decision;}
    public long getEventDateTime(){return this.eventDateTime;}
//    public long getPrefDateTime(){return this.prefDateTime.getTimeInMillis();}
//    public Boolean getActive(){return this.active;}

    @Exclude
    public ArrayList<String> getDisplayDetails(){
        ArrayList<String> display = new ArrayList<>();
        display.add(this.name);
        display.add(this.group);
        display.add(Long.toString(this.eventDateTime));
        display.add(this.status);
//        if (!this.status.equals("Decided"))
//            display.add(this.prefDateTime.toString());
//        if (!this.decision.equals("Undecided"))
//            display.add(this.decision);
        return display;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(group);
        parcel.writeString(host);
        parcel.writeString(location);
        parcel.writeString(budget);
        parcel.writeString(status);
//        parcel.writeByte((byte) (active == null ? 0 : active ? 1 : 2));
        parcel.writeString(decision);
    }
}