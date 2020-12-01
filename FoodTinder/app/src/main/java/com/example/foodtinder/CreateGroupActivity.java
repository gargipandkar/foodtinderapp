package com.example.foodtinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class CreateGroupActivity extends AppCompatActivity {
    Button btn_create_grp;
    EditText grpName;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_groupcreation);

        btn_create_grp = findViewById(R.id.btn_create_group);
        grpName = findViewById(R.id.in_grpName);

        btn_create_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CREATE GROUP OBJECT AND SEND TO FIREBASE
                final Group currGroup = Group.createGroup(grpName.getText().toString());

                Intent displayLink = new Intent(CreateGroupActivity.this, DisplayGroupLink.class);
                displayLink.putExtra("grpId", currGroup.id);
                startActivity(displayLink);

            }
        });
    }
}
