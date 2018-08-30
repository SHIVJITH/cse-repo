package com.example.sabari.csedept;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mprofile;
    private Button mnewprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mprofile= (Button) findViewById(R.id.profile_btn);
        mnewprofile = findViewById(R.id.profile_view);

        mprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainintent = new Intent (MainActivity.this, scan_profile.class);
                startActivity(mainintent);
            }
        });
    }
}
