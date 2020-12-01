
package com.example.foodtinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayGroupLink extends AppCompatActivity {

    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_grouplink);

        String grpId = getIntent().getStringExtra("grpId");
        Group currGroup = new Group(grpId);

        TextView grpLink = findViewById(R.id.out_groupLink);
        Button btn_shareLink = findViewById(R.id.btn_shareLink);

        grpLink.setText("https://foodtinder.page.link");
        //TODO get and display group link

        btn_shareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShareClicked();
            }
        });
    }

    private void onShareClicked() {
        Uri link = Uri.parse("https://foodtinder.page.link"); //GET GROUP'S UNIQUE LINK

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, link.toString());

        startActivity(Intent.createChooser(intent, "Share Link"));
    }

}