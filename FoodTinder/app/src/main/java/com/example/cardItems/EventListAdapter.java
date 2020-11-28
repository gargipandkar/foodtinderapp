package com.example.cardItems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodtinder.HomeFragment;
import com.example.foodtinder.R;

import java.util.ArrayList;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.MyViewHolder> {


    private ArrayList<EventItem> eventItemArrayList;
    Context context;
    private myOnItemClickListener mListener;

    public interface myOnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(myOnItemClickListener listener){
        this.mListener = listener;
    }

    public EventListAdapter(ArrayList<EventItem> eventItemArrayList, Context context) {
        this.eventItemArrayList = eventItemArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public EventListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card_view, parent, false);

        return new MyViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventListAdapter.MyViewHolder holder, int position) {
        holder.imageView.setImageResource(eventItemArrayList.get(position).getmEventImageResource());
        holder.eventName.setText(eventItemArrayList.get(position).getmEventName());
        holder.eventDT.setText(eventItemArrayList.get(position).getmEventDateTime());
        holder.eventL.setText(eventItemArrayList.get(position).getmEventLocation());
    }

    @Override
    public int getItemCount() {
        return eventItemArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView eventName, eventDT, eventL;

        public MyViewHolder(@NonNull View itemView, myOnItemClickListener listener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.card_event_dp);
            eventName = itemView.findViewById(R.id.card_event_name);
            eventDT = itemView.findViewById(R.id.card_date_time);
            eventL = itemView.findViewById(R.id.card_location);

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
