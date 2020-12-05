package com.example.cardItems;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodtinder.Event;
import com.example.foodtinder.R;

import java.util.ArrayList;

public class EventListAdapterFinal extends RecyclerView.Adapter<EventListAdapterFinal.MyViewHolder> {


    private static final String TAG = "EventListAdapterFinal";
    private ArrayList<Event> eventArrayList;
    Context context;
    private myOnItemClickListener mListener;

    public interface myOnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(myOnItemClickListener listener){
        this.mListener = listener;
    }

    public EventListAdapterFinal(ArrayList<Event> eventArrayList, Context context) {
        this.eventArrayList = eventArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public EventListAdapterFinal.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card_view, parent, false);

        return new MyViewHolder(v, mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EventListAdapterFinal.MyViewHolder holder, int position) {
        holder.eventName.setText(eventArrayList.get(position).getName());
        holder.eventDT.setText("Date: " + eventArrayList.get(position).getDateTimeString());
        holder.eventDecision.setText("Location: " + eventArrayList.get(position).getDecision());
        holder.eventStatus.setText("Status: " + eventArrayList.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView eventName, eventDT, eventDecision, eventStatus;

        public MyViewHolder(@NonNull View itemView, final myOnItemClickListener listener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.card_event_dp);
            eventName = itemView.findViewById(R.id.card_event_name);
            eventDT = itemView.findViewById(R.id.card_date_time);
            eventDecision = itemView.findViewById(R.id.card_decision);
            eventStatus = itemView.findViewById(R.id.card_status);

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
