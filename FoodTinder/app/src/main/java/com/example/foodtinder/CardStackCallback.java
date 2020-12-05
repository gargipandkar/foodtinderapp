package com.example.foodtinder;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

public class CardStackCallback extends DiffUtil.Callback {

    private ArrayList<String> restaurantArrayList;

    public CardStackCallback(ArrayList<String> restaurantArrayList) {
        this.restaurantArrayList = restaurantArrayList;
    }

    public ArrayList<String> getRestaurantArrayList() {
        return restaurantArrayList;
    }

    @Override
    public int getOldListSize() {
        return 0;
    }

    @Override
    public int getNewListSize() {
        return 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }
}
