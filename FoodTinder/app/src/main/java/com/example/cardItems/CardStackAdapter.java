package com.example.cardItems;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodtinder.Group;
import com.example.foodtinder.R;
import com.example.foodtinder.Restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.MyViewHolder> {

    private static final String TAG = "CardStackAdapter";
    private ArrayList<String> restName;
    private myOnRestItemClickListener mListener;
    Context context;

    public interface myOnRestItemClickListener {
        void onItemClick(int position);
    }

    public void setOnRestItemClickListener(myOnRestItemClickListener listener){
        this.mListener = listener;
        this.context = context;
    }

    public CardStackAdapter(ArrayList<String> restName) {
        Log.i(TAG, "listing rest name: " + restName.toString());
        this.restName = restName;
    }

    @NonNull
    @Override
    public CardStackAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_card_view, parent, false);
        return new MyViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CardStackAdapter.MyViewHolder holder, int position) {
//        Log.i(TAG, restName.get(position));
//        holder.name.setText("hello");
    }

    @Override
    public int getItemCount() {
        return restName.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;

        public MyViewHolder(@NonNull View itemView, final myOnRestItemClickListener listener) {
            super(itemView);
//            image = itemView.findViewById(R.id.card_rest_dp);
            name = itemView.findViewById(R.id.item_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }


}
