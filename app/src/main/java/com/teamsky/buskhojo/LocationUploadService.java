package com.teamsky.buskhojo;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationUploadService extends Service {
    private final IBinder mBinder = new MyBinder();
    private static final String CHANNEL_ID = "2";
    private FusedLocationProviderClient locationClient;
    private LocationCallback locationCallback;
    private FirebaseUser user;
    private DatabaseReference driversDatabaseReference;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        user = FirebaseAuth.getInstance().getCurrentUser();
        driversDatabaseReference = FirebaseDatabase.getInstance().getReference("DriversDatabase");
        buildNotification();
        requestLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private void buildNotification() {
        String stop = "stop";
        Intent stopIntent = new Intent(this, LocationUploadService.class);
        stopIntent.setAction(stop);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Driver Mode is Active")
                .setOngoing(true)
                .setContentIntent(stopPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);
            channel.setDescription("Driver Mode is Active");
            channel.setSound(null, null);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        startForeground(1, builder.build());
    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(1000);
        request.setFastestInterval(3000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    LatLng driverLocation = new LatLng(latitude, longitude);
                    driversDatabaseReference.child(user.getUid()).child("Location").setValue(driverLocation);
                }
            };
            locationClient.requestLocationUpdates(request, locationCallback, null);
        } else {
            stopSelf();
        }
    }

    private void stopLocationUpdates() {
        if (locationClient != null && locationCallback != null) {
            locationClient.removeLocationUpdates(locationCallback);
        }
    }

    public class MyBinder extends Binder {
        public LocationUploadService getService() {
            return LocationUploadService.this;
        }
    }
}
