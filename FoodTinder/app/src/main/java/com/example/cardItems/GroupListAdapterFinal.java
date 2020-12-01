package com.example.cardItems;

import android.content.Context;
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

    public GroupListAdapterFinal(ArrayList<Group> groupItemArrayList, Context context) {
        this.groupItemArrayList = groupItemArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public GroupListAdapterFinal.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_card_view, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupListAdapterFinal.MyViewHolder holder, int position) {
//        holder.imageView.setImageResource(groupItemArrayList.get(position).mGroupImageResource());
        holder.groupName.setText(groupItemArrayList.get(position).getName());
        holder.groupMembers.setText(Integer.toString(groupItemArrayList.get(position).getMemberCount()));
//        holder.groupUsers.setText(groupItemArrayList.get(position).mGroupUsers());
    }

    @Override
    public int getItemCount() {
        return groupItemArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView groupName, groupMembers, groupUsers;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.card_group_dp);
            groupName = itemView.findViewById(R.id.card_group_name);
            groupMembers = itemView.findViewById(R.id.card_group_events);
            groupUsers = itemView.findViewById(R.id.card_group_users);

        }
    }
}
