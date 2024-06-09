package com.teamsky.buskhojo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences("NestTechPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        String ModeType = preferences.getString("LoginType", "NotLogin");
        if (isLoggedIn) {
            if (ModeType.equals("UserLogin")){
                startActivity(new Intent(MainActivity.this, UserHomeActivity.class));
                finish();
            }
            else if (ModeType.equals("DriverLogin")){
                startActivity(new Intent(MainActivity.this, DriverHomeActivity.class));
                finish();
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout UserLogin = findViewById(R.id.UserLogin);
        LinearLayout DriverLogin = findViewById(R.id.DriverLogin);

        UserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, UserLoginActivity.class);
                startActivity(intent);
            }
        });

        DriverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, DriverLoginActivity.class);
                startActivity(intent);
            }
        });

    }

}