package com.teamsky.buskhojo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchBusActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DriverAdapter driverAdapter;
    private List<Driver> driversList;
    private ImageView backBtn, exchangePlaces;
    private EditText from, to, datetime;
    private Button searchbutton;
    private DatabaseReference driversDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bus);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        driversDatabaseReference = database.getReference("DriversDatabase");

        recyclerView = findViewById(R.id.searchRecyclerView);
        driversList = new ArrayList<>();
        driverAdapter = new DriverAdapter(driversList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(driverAdapter);


//        backBtn = findViewById(R.id.backBtn);
//        exchangePlaces = findViewById(R.id.exchangePlaces);
        from = findViewById(R.id.fromDes);
        to = findViewById(R.id.toDes);
//        datetime = findViewById(R.id.datetime);
        searchbutton = findViewById(R.id.SerBusBtn);

//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//
//        exchangePlaces.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String temp = from.getText().toString();
//                from.setText(to.getText().toString());
//                to.setText(temp);
//            }
//        });

        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startingPlace = from.getText().toString().toLowerCase().trim();
                String endingPlace = to.getText().toString().toLowerCase().trim();
//                String Date = datetime.getText().toString().trim();

                if (startingPlace.isEmpty()){
                    from.setError("Starting Point Can't be Empty");
                } else if (endingPlace.isEmpty()){
                    from.setError("Ending Point Can't be Empty");
                } else {


                    driversDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<String> matchingDrivers = new ArrayList<>();
                            for (DataSnapshot driverSnapshot : dataSnapshot.getChildren()) {
                                String route = driverSnapshot.child("RouteChart").getValue(String.class).toLowerCase();

                                if (route != null && route.contains(startingPlace) && route.contains(endingPlace)) {
                                    // Add the driverBusNumber to the list of matching drivers
                                    String driverBusNumber = driverSnapshot.getKey();
                                    matchingDrivers.add(driverBusNumber);
                                }
                            }

                            driversList.clear();

                            if (!matchingDrivers.isEmpty()) {
                                for (String driverBusNumber : matchingDrivers) {
                                    // Fetch details for each matching driverBusNumber
                                    driversDatabaseReference.child(driverBusNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            // Retrieve driver details
                                            Driver driver = dataSnapshot.getValue(Driver.class);
                                            if (driver != null) {
                                                driversList.add(driver);
                                                driverAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle database errors, if any
                                            Toast.makeText(SearchBusActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                //resultTextView.setText("No matching drivers found.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(SearchBusActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });

    }
}