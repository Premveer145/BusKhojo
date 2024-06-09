package com.teamsky.buskhojo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrackBusActivity extends AppCompatActivity {

    private EditText busNum;
    private Button tracksearchbutton;
    private ImageView trackback;
    private DatabaseReference driversDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_bus);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        driversDatabaseReference = database.getReference("DriversDatabase");

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        busNum = findViewById(R.id.busNum);
//        trackback = findViewById(R.id.trackback);
        tracksearchbutton = findViewById(R.id.SerBusBtn);

//        trackback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });


        tracksearchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CardView Card = findViewById(R.id.Card);

                Card.setVisibility(8);

                String driverBusNumber = busNum.getText().toString().toLowerCase().trim();

                driversDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isFound = false;
                        for (DataSnapshot driverSnapshot : dataSnapshot.getChildren()) {
                            String busNumber = driverSnapshot.child("DriverBusNumber").getValue(String.class).toLowerCase();

                            if (busNumber != null && busNumber.equals(driverBusNumber)) {
                                String driverName = driverSnapshot.child("DriverName").getValue(String.class);
                                String driverBusName = driverSnapshot.child("DriverBusName").getValue(String.class);
                                String driverContact = driverSnapshot.child("DriverContact").getValue(String.class);
                                String busSeats = driverSnapshot.child("BusSeats").getValue(String.class);
                                String driverBusNumber = driverSnapshot.child("DriverBusNumber").getValue(String.class);
                                String driverUID = driverSnapshot.getKey().toString();

                                TextView track_busName = findViewById(R.id.track_busName);
                                TextView track_busNumber = findViewById(R.id.track_busNumber);
                                TextView track_driverName = findViewById(R.id.track_driverName);
                                TextView track_driverNumber = findViewById(R.id.track_driverNumber);
                                TextView track_seats = findViewById(R.id.track_seats);


                                track_busName.setText("Bus Name: " + driverBusName);
                                track_busNumber.setText("Bus Number: " + driverBusNumber);
                                track_driverName.setText("Driver Name: " + driverName);
                                track_driverNumber.setText("Contact: " + driverContact);
                                track_seats.setText("Seats: " + busSeats);

                                Card.setVisibility(0);
                                Card.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                            Intent intentMap = new Intent();
                                            intentMap.setClass(getApplicationContext(), UserMapsActivity.class);
                                            intentMap.putExtra("DriverUID",driverUID);
                                            startActivity(intentMap);
                                        } else {
                                            requestAppPermissions();
                                        }
                                    }
                                });
                                isFound = true;
                                break;
                            }
                        }
                        if (!isFound) {
                            // Driver with the given DriverBusNumber does not exist
                            Toast.makeText(TrackBusActivity.this, "Bus Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle database errors, if any
                        Toast.makeText(TrackBusActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        
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
                            resolvable.startResolutionForResult(TrackBusActivity.this, 123);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }

                    }

                });

    }

}