package com.example.foodtinder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateGroupFragment extends Fragment {

    public static final String TAG = "CreateGroupFragment";
    Button btn_create_grp;
    EditText grpName;
    String currLink;


    private CreateGroupFragmentListener listener;
    public interface CreateGroupFragmentListener {
        void onNewGroupUpdate(String groupId, Group grp);
    }

//    public CreateEventFragment() {
//        // Required empty public constructor
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_create_group, container, false);

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity)getActivity()).setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity)getActivity()).setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).setTitle("Create New Group");


        btn_create_grp = v.findViewById(R.id.btn_create_group);
        grpName = v.findViewById(R.id.in_grpName);


        //TODO call to build link with grpId as parameter

        btn_create_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CREATE GROUP OBJECT AND SEND TO FIREBASE
                final Group currGroup = Group.createGroup(grpName.getText().toString());

                Group currentGroup = new Group(currGroup.id);
                currLink = currentGroup.getShareableLink(currGroup.id);


                Log.i(TAG, "CreateGroupFragment getId");
                Log.i(TAG, User.getId());
                Log.i(TAG, currLink);

//                Intent displayLink = new Intent(CreateGroupActivity.this, DisplayGroupLink.class);
//                displayLink.putExtra("grpId", currGroup.id);
//                startActivity(displayLink);

                listener.onNewGroupUpdate(currGroup.id, currentGroup);


            }
        });


        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CreateGroupFragmentListener){
            listener = (CreateGroupFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement CreateGroupFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
