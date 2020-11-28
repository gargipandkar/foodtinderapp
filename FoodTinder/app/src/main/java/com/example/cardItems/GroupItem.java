package com.example.cardItems;

public class GroupItem {

    private int mGroupImageResource;
    private String mGroupName, mGroupEvents, mGroupUsers;

    public GroupItem(int mGroupImageResource, String mGroupName, String mGroupEvents, String mGroupUsers){
        this.mGroupImageResource = mGroupImageResource;
        this.mGroupName = mGroupName;
        this.mGroupEvents = mGroupEvents;
        this.mGroupUsers = mGroupUsers;
    }

    public int mGroupImageResource() {
        return mGroupImageResource;
    }

    public String mGroupName() {
        return mGroupName;
    }

    public String mGroupEvents() {
        return mGroupEvents;
    }

    public String mGroupUsers() {
        return mGroupUsers;
    }

}
