package com.example.foodtinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Dev extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super .onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button enter_btn = findViewById(R.id.enter_btn);
        enter_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("Check", "Button clicked");
                Intent toCreateEvent = new Intent(Dev.this,CreateEventActivity.class);
                startActivity(toCreateEvent);
            }
        });

    }
}
