package com.example.foodtinder;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardItems.EventItem;
//import com.example.cardItems.EventListAdapter;
import com.example.cardItems.GroupItem;
import com.example.cardItems.GroupListAdapter;
import com.example.cardItems.GroupListAdapterFinal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class GroupFragment extends Fragment {

    private FragmentGroupListener groupListener;
    private RecyclerView recyclerView;
    private ArrayList<Group> groupItemArrayList;
    GroupListAdapterFinal groupListAdapter;
    ArrayList<Group> groupsInfoList = new ArrayList<>();

    public static final String TAG = "GroupFragment";


    public interface FragmentGroupListener {
        void onCreateGroup();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_group, container, false);

//        createGroupList();
        buildRecycleView(v);

        FloatingActionButton createGroup_btn = v.findViewById(R.id.create_group_btn);
        createGroup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            groupsInfoList = bundle.getParcelableArrayList("grouplist");
            Log.i(TAG, groupsInfoList.toString());
            Log.i(TAG, "group list delivered to groupfragment");
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
