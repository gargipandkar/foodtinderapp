package com.example.foodtinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity{

    // TAG for debugging purposes
    private static final String TAG = "SignInActivity";

    private static final int RC_SIGN_IN = 120;
    private FirebaseAuth mAuth;
    private static GoogleSignInClient mGoogleSignInClient;

    DatabaseReference db, users_ref;

    private ArrayList allUsers = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super .onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signin);

        boolean fromDynamicLink = getIntent().getBooleanExtra("EXTRA_NOT_SIGNEDIN_DL", false);
        if (fromDynamicLink == true){
            Intent toDynamicLink = new Intent(SignInActivity.this, GroupLandingPage.class);
            toDynamicLink.putExtra("EXTRA_NOT_SIGNEDIN_DL_SIGNED", true);
            startActivity(toDynamicLink);
        }

        SignInButton googleSignInButton = findViewById(R.id.sign_in_button);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialise Firebase database references
        db = FirebaseDatabase.getInstance().getReference();
        users_ref = db.child("USERS");

        users_ref.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                for (DataSnapshot user: dataSnapshot.getChildren())
                    allUsers.add(user.getKey());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Check", databaseError.toException());
            }
        });

        // Sign in button
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    // Function to call to authenticate Firebase using Google
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser(); // can remove if user data is not needed
                            Log.w(TAG, allUsers.toString());
                            fillUserDetails(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    // Function to call to redirect user to correct page
    // If user is signed in, redirect to SignOutActivity
    // Else, redirect back to Sign in page (SignInActivity)
    private void updateUI(FirebaseUser user) {
        if (user != null){
            // User is signed in
            Intent toListEvent = new Intent (SignInActivity.this, SignOutActivity.class);
            startActivity(toListEvent);
            finish();
        } else {
            // Unable to sign in
            Intent toSignIn = new Intent (SignInActivity.this, SignInActivity.class);
            startActivity(toSignIn);
            finish();
        }
    }

    // Function to call to create a new User object
    // Add user to Firebase Realtime Database
    private void fillUserDetails(FirebaseUser user){
        if (user != null) {
            String id = user.getUid();
            User currUser = new User(id, user.getDisplayName(), user.getEmail());
            if (!allUsers.contains(id)) {
                users_ref.child(id).child("id").setValue(User.getId());
                users_ref.child(id).child("name").setValue(User.getName());
                users_ref.child(id).child("email").setValue(User.getEmail());
            }

        }
    }

}
