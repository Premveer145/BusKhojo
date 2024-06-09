package com.teamsky.buskhojo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserLoginActivity extends AppCompatActivity {
    private TextView timerTextView;
    private CountDownTimer countDownTimer;
    private FirebaseAuth mAuth;
    private SharedPreferences preferences;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        preferences = getSharedPreferences("NestTechPrefs", MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);

        EditText UserMail = findViewById(R.id.UserMail);
        EditText UserPassword = findViewById(R.id.UserPassword);
        Button UserLoginBtn = findViewById(R.id.UserLoginBtn);
        TextView tvRagisterHere = findViewById(R.id.tvRagisterHere);
        TextView tvForgot = findViewById(R.id.tvForgot);
        LinearLayout backArrow = findViewById(R.id.backArrow);
        timerTextView = findViewById(R.id.timerTextView);

        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update the timer text with the remaining time
                long seconds = millisUntilFinished / 1000;
                timerTextView.setText("Resend email in: " + String.format("%02d:%02d", seconds / 60, seconds % 60));
            }

            public void onFinish() {
                // Timer finished, allow email resend or update UI as needed
                timerTextView.setText("Send Email.");
                timerTextView.setClickable(true);
            }
        };

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvRagisterHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserLoginActivity.this, UserSignupActivity.class);
                startActivity(intent);
            }
        });

        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserLoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        UserLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uMail = UserMail.getText().toString();
                String uPass = UserPassword.getText().toString();

                if (uMail.isEmpty()){
                    UserMail.setError("Email Address Can't be Empty");
                }
                else if (uPass.isEmpty()){
                    UserPassword.setError("Password Can't be Empty");
                }
                else {
                    progressDialog.setMessage("Please Wait While Login...");
                    progressDialog.setTitle("Login");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(uMail, uPass)
                            .addOnCompleteListener(UserLoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        DatabaseReference userRef = database.getReference(String.format("UsersDatabase/%s",user.getUid()));

                                        userRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String loginType = "";
                                                String userName = "";
                                                String userMail = "";
                                                if (dataSnapshot.child("LoginType").exists()) {
                                                    loginType = (String) dataSnapshot.child("LoginType").getValue();
                                                    userName = dataSnapshot.child("UserName").getValue(String.class);
                                                    userMail = user.getEmail();
                                                }
                                                if (loginType.equals("UserLogin")) {
                                                    if (user.isEmailVerified()){
                                                        SharedPreferences.Editor editor = preferences.edit();
                                                        editor.putBoolean("isLoggedIn", true);
                                                        editor.putString("LoginType", "UserLogin");
                                                        editor.putString("UserName", userName);
                                                        editor.putString("UserMail", userMail);
                                                        editor.apply();
                                                        Intent intentHome = new Intent();
                                                        intentHome.setClass(UserLoginActivity.this, UserHomeActivity.class);
                                                        startActivity(intentHome);
                                                        finishAffinity();
                                                    }
                                                    else {
                                                        timerTextView.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                EmailVerification(user);
                                                            }
                                                        });
                                                        EmailVerification(user);
                                                    }
                                                } else {
                                                    Toast.makeText(UserLoginActivity.this, "No User Account Found with this Email.", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                System.out.println("Error reading from the database: " + databaseError.toException());
                                            }
                                        });

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(UserLoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });

    }

    private void EmailVerification(FirebaseUser user){
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserLoginActivity.this,"Email Verification Send.",Toast.LENGTH_SHORT).show();
                            startTimer();
                        } else {
                            Toast.makeText(UserLoginActivity.this,"Email Verification Already Send. Please Wait and Try Again Later.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void startTimer() {
        timerTextView.setClickable(false);
        countDownTimer.start();
    }

}