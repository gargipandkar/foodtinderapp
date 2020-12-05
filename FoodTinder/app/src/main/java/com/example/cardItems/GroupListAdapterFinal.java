package com.example.cardItems;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodtinder.Group;
import com.example.foodtinder.R;

import java.util.ArrayList;

public class GroupListAdapterFinal extends RecyclerView.Adapter<GroupListAdapterFinal.MyViewHolder> {


    ArrayList<Group> groupItemArrayList;
    Context context;
    private myOnGroupItemClickListener mListener;

    public interface myOnGroupItemClickListener {
        void onItemClick(int position);
    }

    public void setOnGroupItemClickListener(myOnGroupItemClickListener listener){
        this.mListener = listener;
    }


    public GroupListAdapterFinal(ArrayList<Group> groupItemArrayList, Context context) {
        this.groupItemArrayList = groupItemArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public GroupListAdapterFinal.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_card_view, parent, false);

        return new MyViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupListAdapterFinal.MyViewHolder holder, int position) {
//        holder.imageView.setImageResource(groupItemArrayList.get(position).mGroupImageResource());
        holder.groupName.setText(groupItemArrayList.get(position).getName());
        holder.groupMembers.setText("No. of members: " + Integer.toString(groupItemArrayList.get(position).getMemberCount()));
//        holder.groupLink.setText("link");
//        Log.i("checking link", groupItemArrayList.get(position).getLink());
        holder.groupLink.setText("Share link: " + groupItemArrayList.get(position).getLink());
    }

    @Override
    public int getItemCount() {
        return groupItemArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView groupName, groupMembers, groupUsers, groupLink;

        public MyViewHolder(@NonNull View itemView, final myOnGroupItemClickListener listener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.card_group_dp);
            groupName = itemView.findViewById(R.id.card_group_name);
            groupMembers = itemView.findViewById(R.id.card_group_events);
            groupLink = itemView.findViewById(R.id.card_group_link);

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
