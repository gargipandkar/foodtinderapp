package com.example.foodtinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    private FragmentProfileListener profileListener;
    private Button logout_btn;
    private ImageView profilePic;
    private TextView username_detail, email_detail;
    private User user_details;
    private FirebaseAuth mAuth;


    public interface FragmentProfileListener {
        void onLogout();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_profile, container, false);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();



        username_detail = v.findViewById(R.id.username);
        email_detail = v.findViewById(R.id.email);
        profilePic = v.findViewById(R.id.profilePicture);

        updateProfile(currentUser);

        logout_btn = v.findViewById(R.id.signout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileListener.onLogout();
            }
        });

        return v;
    }

    private void updateProfile(FirebaseUser currentUser) {
        // get user data from Firebase
//        String name = currentUser.getDisplayName();
//        String email = currentUser.getEmail();
//        Uri photoUrl = currentUser.getPhotoUrl();

        // get user data from User class
        String name = User.getName();
        String email = User.getEmail();

        // update username, email and profile picture
        username_detail.setText(name);
        email_detail.setText(email);
//        profilePic.setImageURI(photoUrl);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentProfileListener){
            profileListener = (FragmentProfileListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentProfileListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        profileListener = null;
    }
}
