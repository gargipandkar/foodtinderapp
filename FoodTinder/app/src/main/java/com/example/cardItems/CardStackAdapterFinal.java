package com.example.cardItems;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodtinder.R;

import java.util.ArrayList;
import java.util.HashMap;

//public class CardStackAdapterFinal extends RecyclerView.Adapter<CardStackAdapterFinal.MyViewHolder> {
//
//    private static final String TAG = "CardStackAdapter";
//    private ArrayList<String> restName;
//    private myOnRestItemClickListener mListener;
//
//    public interface myOnRestItemClickListener {
//        void onItemClick(int position);
//    }
//
//    public void setOnRestItemClickListener(myOnRestItemClickListener listener){
//        this.mListener = listener;
//    }
//
//    public CardStackAdapterFinal(ArrayList<String> restName) {
//        this.restName = restName;
//    }
//
//    @NonNull
//    @Override
//    public CardStackAdapterFinal.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_card_view, parent, false);
//        return new MyViewHolder(v, mListener);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CardStackAdapterFinal.MyViewHolder holder, int position) {
//        Log.i(TAG, restName.get(position));
////        holder.name.setText("hello");
//    }
//
//    @Override
//    public int getItemCount() {
//        return restName.size();
//    }
//
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//
//        ImageView image;
//        TextView name;
//
//        public MyViewHolder(@NonNull View itemView, final myOnRestItemClickListener listener) {
//            super(itemView);
//            image = itemView.findViewById(R.id.card_rest_dp);
//            name = itemView.findViewById(R.id.card_rest_name);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (listener != null){
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            listener.onItemClick(position);
//                        }
//                    }
//                }
//            });
//
//        }
//    }
//
//
//}


public class CardStackAdapterFinal extends RecyclerView.Adapter<CardStackAdapterFinal.ViewHolder> {

    private ArrayList<String> items;

    public CardStackAdapterFinal(ArrayList<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.swipe_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nama.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView nama, usia, kota;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.item_name);

        }

//        void setData(ItemModel data) {
//            Picasso.get()
//                    .load(data.getImage())
//                    .fit()
//                    .centerCrop()
//                    .into(image);
//            nama.setText(data.getNama());
//            usia.setText(data.getUsia());
//            kota.setText(data.getKota());
//        }
    }

//    public List<ItemModel> getItems() {
//        return items;
//    }
//
//    public void setItems(List<ItemModel> items) {
//        this.items = items;
//    }
}