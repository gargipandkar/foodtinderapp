package com.example.foodtinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardItems.GroupListAdapterFinal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class GroupFragment extends Fragment {

    // TAG for debugging purposes
    public static final String TAG = "GroupFragment";

    private RecyclerView recyclerView;
    GroupListAdapterFinal groupListAdapter;
    ArrayList<Group> groupsInfoList = new ArrayList<>();
    boolean inGroup = false; // Initialise first to prevent any error

    // Listener is used to call the abstract method in the interface
    private FragmentGroupListener groupListener;

    // Implement this interface in host Activity (SignOutActivity.java) to transfer data from this Fragment to host Activity
    // Abstract method will be override in host Activity to receive information needed and communicate with the next fragment
    public interface FragmentGroupListener {
        void onCreateGroup();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_group, container, false);

        // If user is not in any groups, display "Not in any group"
        // Else generate a list of groups user is in
        if (inGroup == false){
            // no new upcoming groups
            TextView noGroupText = v.findViewById(R.id.no_group_label);
            TextView createEventText = v.findViewById(R.id.create_group_label);
            noGroupText.setText("Not in any group");
            createEventText.setText("Create new group");
        } else {
            buildRecycleView(v);
        }

        FloatingActionButton createGroup_btn = v.findViewById(R.id.create_group_btn);
        createGroup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to host Activity (SignOutActivity.java)
                groupListener.onCreateGroup();
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        // If user is in one or more group, get the list of groups and set inGroup as true
        // Else inGroup is false by default
        // To ensure proper display on the screen
        if (bundle != null){
            groupsInfoList = (ArrayList<Group>) bundle.getSerializable("grouplist");
            inGroup = true;
        }
    }

    // Function to call if user is in one or more group
    // Returns a list of groups user is in
    private void buildRecycleView(View v) {
        recyclerView = v.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        groupListAdapter = new GroupListAdapterFinal(groupsInfoList, getContext());
        recyclerView.setAdapter(groupListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Allows user to click on the group
        // Returns a dynamic link for user to share and invite others into the group
        groupListAdapter.setOnGroupItemClickListener(new GroupListAdapterFinal.myOnGroupItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, groupsInfoList.get(position).getLink());
                startActivity(Intent.createChooser(sendIntent, "Share Link"));
            }
        });
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentGroupListener){
            groupListener = (FragmentGroupListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentProfileListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        groupListener = null;
    }
}
