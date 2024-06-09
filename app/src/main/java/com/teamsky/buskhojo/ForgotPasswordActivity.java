package com.teamsky.buskhojo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        EditText forgotMail = findViewById(R.id.forgotMail);
        Button sendbutton = findViewById(R.id.sendBtn);
        LinearLayout backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = forgotMail.getText().toString().trim();

                if (email.isEmpty()) {
                    forgotMail.setError("Email Address Can't be Empty");
                } else {
                    progressDialog.setMessage("Please Wait While Sending Mail...");
                    progressDialog.setTitle("Sending Mail");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent. Check your inbox.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send password reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
}