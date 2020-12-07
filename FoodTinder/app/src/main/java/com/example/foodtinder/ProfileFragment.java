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
    private TextView username_detail, email_detail;


    public interface FragmentProfileListener {
        void onLogout();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        username_detail = v.findViewById(R.id.username);
        email_detail = v.findViewById(R.id.email);

        updateProfile();

        logout_btn = v.findViewById(R.id.signout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileListener.onLogout();
            }
        });

        return v;
    }

    // Function to call to update Profile Fragment UI
    private void updateProfile() {
        // Get user information from User class
        String name = User.getName();
        String email = User.getEmail();

        // Update username and email on Profile Fragment
        username_detail.setText(name);
        email_detail.setText(email);
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
