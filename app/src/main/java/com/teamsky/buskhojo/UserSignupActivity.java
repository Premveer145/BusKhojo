package com.teamsky.buskhojo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UserSignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private SharedPreferences preferences;
    private DatabaseReference driversDatabaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        driversDatabaseReference = database.getReference("UsersDatabase");
        progressDialog = new ProgressDialog(this);

        EditText UserName = findViewById(R.id.UserName);
        EditText UserMail = findViewById(R.id.UserMail);
        EditText UserPassword = findViewById(R.id.UserPassword);
        EditText UserConPassword = findViewById(R.id.UserConPassword);
        Button UserSignupBtn = findViewById(R.id.UserSignupBtn);
        TextView tvLoginHere = findViewById(R.id.tvLoginHere);
        LinearLayout backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvLoginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        UserSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = UserName.getText().toString().trim();
                String email = UserMail.getText().toString().trim();
                String password = UserPassword.getText().toString().trim();
                String confirmPassword = UserConPassword.getText().toString().trim();

                if (name.isEmpty()){
                    UserName.setError("UserName Can't be Empty");
                }
                else if (email.isEmpty()){
                    UserMail.setError("Email Address Can't be Empty");
                }
                else if (password.isEmpty()){
                    UserPassword.setError("Password Can't be Empty");
                }
                else if (confirmPassword.isEmpty()){
                    UserConPassword.setError("Confirm Password Can't be Empty");
                }
                else {
                    if (!password.equals(confirmPassword)) {
                        Toast.makeText(UserSignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    progressDialog.setMessage("Please Wait While Registration...");
                    progressDialog.setTitle("Registration");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    // Check if the user is already registered
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(UserSignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        // Sign up success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        // Create a Map to hold the data
                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("UserName", name);
                                        userData.put("LoginType", "UserLogin");


                                        // Save the data to Firebase under "DriversDatabase" > "driverBusNumber"
                                        driversDatabaseReference.child(user.getUid()).setValue(userData)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
//                                                            preferences = getSharedPreferences("NestTechPrefs", MODE_PRIVATE);
//                                                            SharedPreferences.Editor editor = preferences.edit();
//                                                            editor.putBoolean("isLoggedIn", true);
//                                                            editor.putString("LoginType", "UserLogin");
//                                                            editor.apply();
                                                            Toast.makeText(UserSignupActivity.this, "Signup successful", Toast.LENGTH_LONG).show();
                                                            finish();
//                                                            Intent intentHome = new Intent();
//                                                            intentHome.setClass(UserSignupActivity.this, UserHomeActivity.class);
//                                                            startActivity(intentHome);
//                                                            finishAffinity();
                                                        } else {
                                                            Toast.makeText(UserSignupActivity.this, "Data upload faild", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(UserSignupActivity.this, "Data upload faild: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });




                                        // You can add code here to navigate to the next activity or perform other tasks.
                                    } else {
                                        progressDialog.dismiss();
                                        // If sign up fails, display a message to the user.
                                        Toast.makeText(UserSignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
}