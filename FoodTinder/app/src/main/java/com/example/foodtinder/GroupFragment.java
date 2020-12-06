package com.example.foodtinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardItems.EventItem;
//import com.example.cardItems.EventListAdapter;
import com.example.cardItems.EventListAdapterFinal;
import com.example.cardItems.GroupItem;
//import com.example.cardItems.GroupListAdapter;
import com.example.cardItems.GroupListAdapterFinal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class GroupFragment extends Fragment {

    private FragmentGroupListener groupListener;
    private RecyclerView recyclerView;
    private ArrayList<Group> groupItemArrayList;
    GroupListAdapterFinal groupListAdapter;
    ArrayList<Group> groupsInfoList = new ArrayList<>();
    boolean checkGroup = false;

    public static final String TAG = "GroupFragment";


    public interface FragmentGroupListener {
        void onCreateGroup();
        void onShareLink(String link);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_group, container, false);

        if (checkGroup == false){
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
                Log.i(TAG, "group fragment create_group_btn clicked");
                groupListener.onCreateGroup();
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null){
            groupsInfoList = (ArrayList<Group>) bundle.getSerializable("grouplist");
            Log.i(TAG, groupsInfoList.toString());
            Log.i(TAG, "group list delivered to groupfragment");
            checkGroup = true;
        }
    }

//    private void createGroupList() {
//        groupItemArrayList = new ArrayList<>();
//        groupItemArrayList.add(new GroupItem(R.drawable.logo_temporary, "Term 4 ISTD Group", "Meeting at LT1, 9pm", "Gargi, Mihir, Bang Yong, Joshua, Liwen"));
//        groupItemArrayList.add(new GroupItem(R.drawable.logo_temporary, "Class 04", "Lesson at DSL, 1.30pm", "Everyone"));
//        groupItemArrayList.add(new GroupItem(R.drawable.logo_temporary, "Class 04", "Lesson at DSL, 1.30pm", "Everyone"));
//    }

    private void buildRecycleView(View v) {
        recyclerView = v.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        groupListAdapter = new GroupListAdapterFinal(groupsInfoList, getContext());
        recyclerView.setAdapter(groupListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        groupListAdapter.setOnGroupItemClickListener(new GroupListAdapterFinal.myOnGroupItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i(TAG, "here");
                Log.i(TAG, Integer.toString(position));
//                eventItemArrayList.get(position).changeText("Clickeddddd");
//                eventListAdapter.notifyItemChanged(position);
//                Log.i(TAG, Integer.toString(position));
//                Log.i(TAG, eventItemArrayList.get(position).toString());
//                Intent toEventSelection = new Intent(getActivity(), EventSelectionActivity.class);
//                toEventSelection.putExtra("Event Item", eventsInfoList.get(position));
//                startActivity(toEventSelection);
                //        Uri link = Uri.parse(currLink); //GET GROUP'S UNIQUE LINK

//                groupListener.onShareLink(groupsInfoList.get(position).getLink());
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
