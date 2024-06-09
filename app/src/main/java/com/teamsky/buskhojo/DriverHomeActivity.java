package com.teamsky.buskhojo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button startShareLoc;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolBar;
    private SharedPreferences preferences;
    private DatabaseReference driverssDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);
        drawerLayout = findViewById(R.id.DriverDrawerLayout);
        navigationView = findViewById(R.id.DriverNavView);
        toolBar = findViewById(R.id.DriverToolBar);

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

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LinearLayout busDetails = findViewById(R.id.BusDetails);
        startShareLoc = findViewById(R.id.locShare);

        busDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(DriverHomeActivity.this, DriverBusDetailsActivity.class);
                startActivity(intent);
            }
        });

        startShareLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (startShareLoc.getText().toString().equals("Start")) {
                        enableLocationSettings();
                    } else {
                        stopLocationUploadService();
                    }
                } else {
                    requestAppPermissions();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isServiceRunning(this, LocationUploadService.class)) {
            startShareLoc.setText("Stop");
        } else {
            startShareLoc.setText("Start");
        }
    }

    public boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    protected void requestAppPermissions() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(1000)
                .setFastestInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        LocationServices
                .getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(this, (LocationSettingsResponse response) -> {
                    // startUpdatingLocation(...);
                })
                .addOnFailureListener(this, ex -> {
                    if (ex instanceof ResolvableApiException) {
                        // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) ex;
                            resolvable.startResolutionForResult(DriverHomeActivity.this, 123);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }

                    }

                });

    }


    private void enableLocationSettings() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permissions are already granted
                startLocationUploadService();
            } else {
                // Request permissions
                requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            // Permissions are granted at installation time (pre-Marshmallow)
            startLocationUploadService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length == permissions.length) {
                boolean allPermissionsGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }

                if (allPermissionsGranted) {
                    // All permissions are granted
                    startLocationUploadService();
                } else {
                    // Permission is denied
                    showSettingsDialog();
                }
            }
        }
    }

    private void startLocationUploadService() {
        Intent startServiceIntent = new Intent(this, LocationUploadService.class);
        startService(startServiceIntent);
        startShareLoc.setText("Stop");
    }

    private void stopLocationUploadService() {
        Intent stopServiceIntent = new Intent(this, LocationUploadService.class);
        stopService(stopServiceIntent);
        startShareLoc.setText("Start");
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverHomeActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        int itemID = item.getItemId();
        if (itemID == R.id.nev_BusDetails) {
            intent = new Intent(DriverHomeActivity.this, SearchBusActivity.class);
            startActivity(intent);
        } else if (itemID == R.id.nev_More) {
            intent = new Intent(DriverHomeActivity.this, RouteChartActivity.class);
            startActivity(intent);
        }  else if (itemID == R.id.nev_Profile) {
            intent = new Intent(DriverHomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (itemID == R.id.nev_LogOut) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.putString("LoginType", "NotLogin");
            editor.putString("UserName","User Name");
            editor.putString("UserMail","usermail@gmail.com");
            editor.apply();
            intent = new Intent(DriverHomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (itemID == R.id.nev_share) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "Check this App\n "+"https://www.mediafire.com/file/m5difpfh8glwfnx/BusKhojo_1.0.apk/file");
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, "Share this app"));
        } else if (itemID == R.id.nev_rate) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DriverHomeActivity.this);
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