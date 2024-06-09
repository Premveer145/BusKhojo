package com.teamsky.buskhojo;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DriverBusDetailsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference driversDatabaseReference;
    private EditText driverBusName, driverBusNumber, driverName, driverContact, busSeats, routechart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_bus_details);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        driversDatabaseReference = database.getReference("DriversDatabase");

        driverBusName = findViewById(R.id.driverBusName);
        driverBusNumber = findViewById(R.id.driverBusNumber);
        driverName = findViewById(R.id.driverName);
        driverContact = findViewById(R.id.driverContact);
        busSeats = findViewById(R.id.busSeats);
        routechart = findViewById(R.id.routechart);
        ImageView driverBusNameEdit = findViewById(R.id.driverBusNameEdit);
        ImageView driverBusNumberEdit = findViewById(R.id.driverBusNumberEdit);
        ImageView driverNameEdit = findViewById(R.id.driverNameEdit);
        ImageView driverContactEdit = findViewById(R.id.driverContactEdit);
        ImageView busSeatsEdit = findViewById(R.id.busSeatsEdit);
        ImageView routechartEdit = findViewById(R.id.routechartEdit);
        Button driverSubmitBtn = findViewById(R.id.driverSubmitBtn);

        setEditTextDisable();
        driverSubmitBtn.setEnabled(false);
        retriveValues();

        driverBusNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driverBusName.setEnabled(true);
                driverSubmitBtn.setEnabled(true);
                driverBusName.requestFocus();
            }
        });
        driverBusNumberEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driverBusNumber.setEnabled(true);
                driverSubmitBtn.setEnabled(true);
                driverBusNumber.requestFocus();
            }
        });
        driverNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driverName.setEnabled(true);
                driverSubmitBtn.setEnabled(true);
                driverName.requestFocus();
            }
        });
        driverContactEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driverContact.setEnabled(true);
                driverSubmitBtn.setEnabled(true);
                driverContact.requestFocus();
            }
        });
        busSeatsEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                busSeats.setEnabled(true);
                driverSubmitBtn.setEnabled(true);
                busSeats.requestFocus();
            }
        });
        routechartEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routechart.setEnabled(true);
                driverSubmitBtn.setEnabled(true);
                routechart.requestFocus();
            }
        });

        driverSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the values from EditText fields
                String busName = driverBusName.getText().toString();
                String busNumber = driverBusNumber.getText().toString();
                String name = driverName.getText().toString();
                String contact = driverContact.getText().toString();
                String seats = busSeats.getText().toString();
                String route = routechart.getText().toString();
                if (busName.isEmpty()){
                    driverBusName.setError("Enter Bus Name");
                } else if (busNumber.isEmpty()) {
                    driverBusNumber.setError("Enter Bus Number");
                } else if (name.isEmpty()) {
                    driverName.setError("Enter Driver's Name");
                } else if (contact.isEmpty()) {
                    driverContact.setError("Enter Driver's Contact");
                } else if (seats.isEmpty()) {
                    busSeats.setError("Enter Number of Seats");
                } else if (route.isEmpty()) {
                    routechart.setError("Enter Routes of bus");
                } else {
                    // Create a Map to hold the data
                    Map<String, Object> driverData = new HashMap<>();
                    driverData.put("DriverBusName", busName);
                    driverData.put("DriverBusNumber", busNumber);
                    driverData.put("DriverName", name);
                    driverData.put("DriverContact", contact);
                    driverData.put("BusSeats", seats);
                    driverData.put("RouteChart", route);

                    // Save the data to Firebase under "DriversDatabase" > "driverBusNumber"
                    driversDatabaseReference.child(user.getUid()).updateChildren(driverData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(DriverBusDetailsActivity.this, "Data uploaded successfully", Toast.LENGTH_SHORT).show();
                                        setEditTextDisable();
                                        driverSubmitBtn.setEnabled(false);
                                    } else {
                                        Toast.makeText(DriverBusDetailsActivity.this, "Data upload faild", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DriverBusDetailsActivity.this, "Data upload faild: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

    }

    private void retriveValues() {

        driversDatabaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                driverBusName.setText(dataSnapshot.child("DriverBusName").getValue(String.class));
                driverBusNumber.setText(dataSnapshot.child("DriverBusNumber").getValue(String.class));
                driverName.setText(dataSnapshot.child("DriverName").getValue(String.class));
                driverContact.setText(dataSnapshot.child("DriverContact").getValue(String.class));
                busSeats.setText(dataSnapshot.child("BusSeats").getValue(String.class));
                routechart.setText(dataSnapshot.child("RouteChart").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    private void setEditTextDisable() {
        int textColor;
        int currentTheme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentTheme == Configuration.UI_MODE_NIGHT_YES) {
            textColor = WHITE;
        } else {
            textColor = BLACK;
        }
        driverBusName.setEnabled(false);
        driverBusName.setTextColor(textColor);
        driverBusName.setAlpha(1f);
        driverBusNumber.setEnabled(false);
        driverBusNumber.setTextColor(textColor);
        driverBusNumber.setAlpha(1f);
        driverName.setEnabled(false);
        driverName.setTextColor(textColor);
        driverName.setAlpha(1f);
        driverContact.setEnabled(false);
        driverContact.setTextColor(textColor);
        driverContact.setAlpha(1f);
        busSeats.setEnabled(false);
        busSeats.setTextColor(textColor);
        busSeats.setAlpha(1f);
        routechart.setEnabled(false);
        routechart.setTextColor(textColor);
        routechart.setAlpha(1f);
    }
}