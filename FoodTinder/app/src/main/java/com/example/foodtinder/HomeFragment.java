package com.example.foodtinder;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardItems.EventItem;
import com.example.cardItems.EventListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FloatingActionButton create_event_btn;
    private FragmentHomeListener homeListener;
    private RecyclerView recyclerView;
    private ArrayList<EventItem> eventItemArrayList;
    EventListAdapter eventListAdapter;

    public interface FragmentHomeListener {
        void onCreateEvent();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        createEventList();
        buildRecycleView(v);


        create_event_btn = v.findViewById(R.id.create_event_btn);
        create_event_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeListener.onCreateEvent();
            }
        });

        return v;
    }

    private void createEventList() {
        eventItemArrayList = new ArrayList<>();
        eventItemArrayList.add(new EventItem(R.drawable.logo_temporary, "Celebrate Christmas", "25/12/2020, 9pm", "Orchard Road"));
        eventItemArrayList.add(new EventItem(R.drawable.logo_temporary, "CNY with Fam", "15/02/2020, 5pm", "Home"));
        eventItemArrayList.add(new EventItem(R.drawable.logo_temporary, "CNY with Fam", "15/02/2020, 5pm", "Home"));
        eventItemArrayList.add(new EventItem(R.drawable.logo_temporary, "CNY with Fam", "15/02/2020, 5pm", "Home"));
        eventItemArrayList.add(new EventItem(R.drawable.logo_temporary, "CNY with Fam", "15/02/2020, 5pm", "Home"));
        eventItemArrayList.add(new EventItem(R.drawable.logo_temporary, "CNY with Fam", "15/02/2020, 5pm", "Home"));
        eventItemArrayList.add(new EventItem(R.drawable.logo_temporary, "CNY with Fam", "15/02/2020, 5pm", "Home"));
        eventItemArrayList.add(new EventItem(R.drawable.logo_temporary, "CNY with Fam", "15/02/2020, 5pm", "Home"));
        eventItemArrayList.add(new EventItem(R.drawable.logo_temporary, "CNY with Fam", "15/02/2020, 5pm", "Home"));
        eventItemArrayList.add(new EventItem(R.drawable.logo_temporary, "CNY with Fam", "15/02/2020, 5pm", "Home"));
        eventItemArrayList.add(new EventItem(R.drawable.logo_temporary, "CNY with Fam", "15/02/2020, 5pm", "Home"));
        eventItemArrayList.add(new EventItem(R.drawable.logo_temporary, "CNY with Fam", "15/02/2020, 5pm", "Home"));

    }

    private void buildRecycleView(View v) {
        recyclerView = v.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        eventListAdapter = new EventListAdapter(eventItemArrayList, getContext());
        recyclerView.setAdapter(eventListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        eventListAdapter.setOnItemClickListener(new EventListAdapter.myOnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                eventItemArrayList.get(position).changeText("Clickeddddd");
                eventListAdapter.notifyItemChanged(position);
            }
        });
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentHomeListener){
            homeListener = (FragmentHomeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentHomeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeListener = null;
    }
}
