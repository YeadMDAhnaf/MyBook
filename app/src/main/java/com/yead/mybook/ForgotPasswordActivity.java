package com.yead.mybook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText edtEmail;
    private Button btnReset;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    String strEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        edtEmail = findViewById(R.id.email);
        btnReset = findViewById(R.id.reset);
        mAuth = FirebaseAuth.getInstance();

        // Initialize the ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending password reset link...");
        progressDialog.setCancelable(false); // Prevent dismissal by clicking outside

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strEmail = edtEmail.getText().toString().trim();
                if (!TextUtils.isEmpty(strEmail)) {
                    ResetPass();
                } else {
                    edtEmail.setError("Enter Email");
                }
            }
        });
    }

    private void ResetPass() {
        // Show the progress dialog before starting the reset process
        progressDialog.show();

        mAuth.sendPasswordResetEmail(strEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Dismiss the progress dialog
                progressDialog.dismiss();

                Toast.makeText(ForgotPasswordActivity.this, "Reset Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();

                // Redirect to the login page after successful email sending
                Intent intent = new Intent(ForgotPasswordActivity.this, loginpage.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Dismiss the progress dialog on failure
                progressDialog.dismiss();

                Toast.makeText(ForgotPasswordActivity.this, "Error. Try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
