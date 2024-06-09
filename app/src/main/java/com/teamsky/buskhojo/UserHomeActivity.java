package com.teamsky.buskhojo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class UserHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolBar;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        LinearLayout SerBus = findViewById(R.id.SerBus);
        LinearLayout RouteChart = findViewById(R.id.RouteChart);
        LinearLayout BusDetails = findViewById(R.id.BusDetails);
        LinearLayout TrackBus = findViewById(R.id.TrackBus);
        LinearLayout History = findViewById(R.id.History);
        LinearLayout More = findViewById(R.id.More);
        drawerLayout = findViewById(R.id.UserDrawerLayout);
        navigationView = findViewById(R.id.UserNavView);
        toolBar = findViewById(R.id.UserToolBar);

        View headerView = navigationView.getHeaderView(0);
        ImageView headUserProfile = headerView.findViewById(R.id.headUserProfile);
        TextView headUserName = headerView.findViewById(R.id.headUserName);
        TextView headUserMail = headerView.findViewById(R.id.headUserMail);

        navigationView.bringToFront();
        setSupportActionBar(toolBar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolBar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        preferences = getSharedPreferences("NestTechPrefs", MODE_PRIVATE);
        headUserName.setText(preferences.getString("UserName","User Name"));
        headUserMail.setText(preferences.getString("UserMail","usermail@gmail.com"));

        SerBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserHomeActivity.this, SearchBusActivity.class);
                startActivity(intent);
            }
        });

        RouteChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserHomeActivity.this, RouteChartActivity.class);
                startActivity(intent);
            }
        });

        BusDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserHomeActivity.this, BusDetailsActivity.class);
                startActivity(intent);
            }
        });

        TrackBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserHomeActivity.this, TrackBusActivity.class);
                startActivity(intent);
            }
        });

        History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserHomeActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserHomeActivity.this, UserMoreActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        int itemID = item.getItemId();
        if (itemID == R.id.nev_serbus) {
            intent = new Intent(UserHomeActivity.this, SearchBusActivity.class);
            startActivity(intent);
        } else if (itemID == R.id.nev_RouteChart) {
            intent = new Intent(UserHomeActivity.this, RouteChartActivity.class);
            startActivity(intent);
        } else if (itemID == R.id.nev_BusDetails) {
            intent = new Intent(UserHomeActivity.this, BusDetailsActivity.class);
            startActivity(intent);
        } else if (itemID == R.id.nev_TrackBus) {
            intent = new Intent(UserHomeActivity.this, TrackBusActivity.class);
            startActivity(intent);
        } else if (itemID == R.id.nev_History) {
            intent = new Intent(UserHomeActivity.this, HistoryActivity.class);
            startActivity(intent);
        } else if (itemID == R.id.nev_More) {
            intent = new Intent(UserHomeActivity.this, UserMoreActivity.class);
            startActivity(intent);
        } else if (itemID == R.id.nev_Profile) {
            intent = new Intent(UserHomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (itemID == R.id.nev_LogOut) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.putString("LoginType", "NotLogin");
            editor.putString("UserName","User Name");
            editor.putString("UserMail","usermail@gmail.com");
            editor.apply();
            intent = new Intent(UserHomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (itemID == R.id.nev_share) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "Check this App\n "+"https://www.mediafire.com/file/m5difpfh8glwfnx/BusKhojo_1.0.apk/file");
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, "Share this app"));
        } else if (itemID == R.id.nev_rate) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserHomeActivity.this);
            builder.setTitle("Rate this app")
                    .setMessage("If you enjoy playing this app, would you mind taking a moment to rate it? It won't take more than a minute.\nThanks for your support!")
                    .setCancelable(false)
                    .setPositiveButton("RATE IT NOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setNegativeButton("NO, THANKS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

}