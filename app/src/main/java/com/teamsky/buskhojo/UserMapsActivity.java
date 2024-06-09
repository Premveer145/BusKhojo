package com.teamsky.buskhojo;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamsky.buskhojo.databinding.ActivityUserMapsBinding;

public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private boolean setFocus = true;
    private boolean locBusToggle = true;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private ActivityUserMapsBinding binding;
    private LocationManager locationManager;
    private Button routeBtn;
    private Location previousLocation = null;
    private DatabaseReference driversDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String driverUID = getIntent().getStringExtra("DriverUID");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        driversDatabaseReference = database.getReference().child("DriversDatabase").child(driverUID).child("Location");

        binding = ActivityUserMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        routeBtn = findViewById(R.id.show_route_button);
        Button locBus = findViewById(R.id.locBus);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        fusedLocationProviderClient = new FusedLocationProviderClient(this);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    //Location location = locationResult.getLastLocation();
                    driversDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Retrieve latitude and longitude values
                                double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                                double longitude = dataSnapshot.child("longitude").getValue(Double.class);

                                // Create a LatLng object
                                LatLng driverLocation = new LatLng(latitude, longitude);

                                // Now, you can use driverLocation as a LatLng object
                                // For example, you can display it on a map
                                updateLocation(driverLocation);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors, if any
                        }
                    });
                }
            }
        };

        routeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        locBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locBusToggle) {
                    setFocus = false;
                    locBusToggle = false;
                    Toast.makeText(UserMapsActivity.this, "Focus Disabled", Toast.LENGTH_SHORT).show();
                } else {
                    setFocus = true;
                    locBusToggle = true;
                    Toast.makeText(UserMapsActivity.this, "Focus Enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setTrafficEnabled(true);
        // Set the desired zoom level without specifying a location
        float desiredZoomLevel = 15.0f;
        CameraUpdate zoomCameraUpdate = CameraUpdateFactory.zoomTo(desiredZoomLevel);

        // Apply the zoom update to the map
        mMap.moveCamera(zoomCameraUpdate);

//        mMap.setOnCameraMoveStartedListener(reason -> {
//            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
//                // The camera move was initiated by a user gesture (e.g., sliding the screen)
//                setFocus = false; // Set setFocus to false when the user slides the screen
//            }
//        });
//        LatLng BusLocation = new LatLng(27.56162, 77.62609);
//        Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bus_marker);
//        markerBitmap = Bitmap.createScaledBitmap(markerBitmap, 100, 100, false);
//        MarkerOptions markerOptions = new MarkerOptions()
//                .position(BusLocation)
//                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
//                .title("Vehicle Location");
//        mMap.addMarker(markerOptions);
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(BusLocation, 15.0f));

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    zoomToUserLocation();
                } else {
                    requestAppPermissions();
                }
                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                AlertDialog.Builder build = new AlertDialog.Builder(UserMapsActivity.this);
                build.setTitle("Vehicle Details");
                build.setMessage("Vehicle Details are not available");
                build.setCancelable(false);
                build.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = build.create();
                alertDialog.show();
            }
        });

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateLocation(LatLng location) {
        //LatLng VehicleLocation = new LatLng(27.56162, 77.62609);
        LatLng VehicleLocation = location;
        mMap.clear();
        Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bus_marker);
        markerBitmap = Bitmap.createScaledBitmap(markerBitmap, 100, 100, false);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(VehicleLocation)
                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
                .title("Vehicle Location");
        mMap.addMarker(markerOptions);
        if (setFocus) {
            float currentZoom = mMap.getCameraPosition().zoom;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(VehicleLocation, currentZoom));
        }

        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(VehicleLocation, 15.0f));
        /*LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //27.6057397,77.591125
        //LatLng latLng = new LatLng(27.60569,77.59322);
        mMap.clear();
        //mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onMapReady(mMap);
        }
    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()), 15.0f));
                }
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
                            resolvable.startResolutionForResult(UserMapsActivity.this, 123);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }

                    }

                });

    }

}