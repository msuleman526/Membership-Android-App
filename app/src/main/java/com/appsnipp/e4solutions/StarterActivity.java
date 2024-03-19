package com.appsnipp.e4solutions;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.appsnipp.e4solutions.Models.Login;
import com.appsnipp.e4solutions.Steps.MainActivity;
import com.appsnipp.e4solutions.Utils.SessionUtil;
import com.appsnipp.e4solutions.Utils.Utils;
import com.appsnipp.e4solutions.Visitors.VisitorListActivity;

public class StarterActivity extends AppCompatActivity {

    CardView convertToMember, createMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);

        convertToMember = findViewById(R.id.convertToMemberView);
        createMember = findViewById(R.id.createMemberView);

        createMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StarterActivity.this, MainActivity.class));
            }
        });

        convertToMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StarterActivity.this, VisitorListActivity.class));
            }
        });

    }
}