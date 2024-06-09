package com.teamsky.buskhojo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        LinearLayout backArrow = findViewById(R.id.backArrow);
        TextView UserNameTxt = findViewById(R.id.pUserNameTxt);
        TextView UserMailTxt = findViewById(R.id.pUserMailTxt);
        EditText UserName = findViewById(R.id.pUserName);
        EditText UserMail = findViewById(R.id.pUserMail);
        Button UpdateBtn = findViewById(R.id.pUserUpdateBtn);

        preferences = getSharedPreferences("NestTechPrefs", MODE_PRIVATE);
        UserNameTxt.setText(preferences.getString("UserName","User Name"));
        UserMailTxt.setText(preferences.getString("UserMail","usermail@gmail.com"));
        UserName.setText(preferences.getString("UserName","User Name"));
        UserMail.setText(preferences.getString("UserMail","usermail@gmail.com"));

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, "Profile Detailes Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });


    }
}