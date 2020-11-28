package com.example.cardItems;

public class EventItem {

    private int mEventImageResource;
    private String mEventName, mEventDateTime, mEventLocation;

    public EventItem(int mEventImageResource, String mEventName, String mEventDateTime, String mEventLocation){
        this.mEventImageResource = mEventImageResource;
        this.mEventName = mEventName;
        this.mEventDateTime = mEventDateTime;
        this.mEventLocation = mEventLocation;
    }

    public void changeText(String text){
        this.mEventName = text;
    }

    public int getmEventImageResource() {
        return mEventImageResource;
    }

    public String getmEventName() {
        return mEventName;
    }

    public String getmEventDateTime() {
        return mEventDateTime;
    }

    public String getmEventLocation() {
        return mEventLocation;
    }

}
