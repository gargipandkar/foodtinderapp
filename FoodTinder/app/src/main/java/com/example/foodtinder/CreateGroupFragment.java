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
import android.widget.Toast;

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

    // TAG for debugging purposes
    public static final String TAG = "CreateGroupFragment";

    // Variables to modify View objects in layou
    Button btn_create_grp;
    EditText grpName;

    String currLink;

    // Listener is used to call the abstract method in the interface
    private CreateGroupFragmentListener listener;

    // Implement this interface in host Activity (SignOutActivity.java) to transfer data from this Fragment to host Activity
    // Abstract method will be override in host Activity to receive information needed and communicate with the next fragment
    public interface CreateGroupFragmentListener {
        void onNewGroupUpdate();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_create_group, container, false);

        // Toolbar is created to give the Fragment a header
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).setTitle("Create New Group");

        // Call View objects in layout
        btn_create_grp = v.findViewById(R.id.btn_create_group);
        grpName = v.findViewById(R.id.in_grpName);


        btn_create_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if all fields are filled
                // If not all fields are valid, send a Toast to alert user and prevent user from creating a group
                // Else a group is created
                if (grpName.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "DO NOT LEAVE FIELD BLANK", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Group cannot be created, field is empty");
                } else {
                    // Create Group object and add to Firebase Realtime Database
                    final Group currGroup = Group.createGroup(grpName.getText().toString());
                    Group currentGroup = new Group(currGroup.id);
                    currLink = currentGroup.getShareableLink(currGroup.id);

                    Log.i(TAG, "Group is successfully created");

                    listener.onNewGroupUpdate();
                }


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
