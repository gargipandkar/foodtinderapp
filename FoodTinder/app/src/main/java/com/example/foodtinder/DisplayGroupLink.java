package com.example.foodtinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayGroupLink extends AppCompatActivity {

    String grpId;
    Group currGroup;
    String currLink;

    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_grouplink);

        grpId = getIntent().getStringExtra("grpId");
        currGroup = new Group(grpId);
        currLink = currGroup.getShareableLink();

        TextView grpLink = findViewById(R.id.out_groupLink);
        Button btn_shareLink = findViewById(R.id.btn_shareLink);

        grpLink.setText(currLink);
        btn_shareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShareClicked();
            }
        });
    }

    private void onShareClicked() {
        Uri link = Uri.parse(currLink); //GET GROUP'S UNIQUE LINK

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, currLink);
        startActivity(Intent.createChooser(sendIntent, "Share Link"));

    }

}
