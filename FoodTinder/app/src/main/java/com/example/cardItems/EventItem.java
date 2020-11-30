package com.example.cardItems;

import android.os.Parcel;
import android.os.Parcelable;

public class EventItem implements Parcelable {

    private int mEventImageResource;
    private String mEventName, mEventDateTime, mEventLocation;

    public EventItem(int mEventImageResource, String mEventName, String mEventDateTime, String mEventLocation){
        this.mEventImageResource = mEventImageResource;
        this.mEventName = mEventName;
        this.mEventDateTime = mEventDateTime;
        this.mEventLocation = mEventLocation;
    }

    protected EventItem(Parcel in) {
        mEventImageResource = in.readInt();
        mEventName = in.readString();
        mEventDateTime = in.readString();
        mEventLocation = in.readString();
    }

    public static final Creator<EventItem> CREATOR = new Creator<EventItem>() {
        @Override
        public EventItem createFromParcel(Parcel in) {
            return new EventItem(in);
        }

        @Override
        public EventItem[] newArray(int size) {
            return new EventItem[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mEventImageResource);
        parcel.writeString(mEventName);
        parcel.writeString(mEventDateTime);
        parcel.writeString(mEventLocation);
    }
}
