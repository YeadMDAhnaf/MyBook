package com.yead.mybook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class newacc extends AppCompatActivity {

    private EditText name, email, number, pass, cpass;
    private Button submit, letsgo;
    private DatabaseReference root;
    private FirebaseAuth mAuth;
    ProgressDialog pd;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newacc);

        // Initialize UI components
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.email);
        number = findViewById(R.id.number);
        pass = findViewById(R.id.password);
        cpass = findViewById(R.id.cPassword);
        submit = findViewById(R.id.submit);
        letsgo = findViewById(R.id.login1);

        pd = new ProgressDialog(this);
        root = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();



        pass.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (pass.getRight() - pass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                    if (isPasswordVisible) {
                        pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        isPasswordVisible = false;
                    } else {
                        pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        isPasswordVisible = true;
                    }

                    pass.setSelection(pass.getText().length());
                    return true;
                }
            }
            return false;
        });


        cpass.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (cpass.getRight() - cpass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    // Toggle password visibility
                    if (isPasswordVisible) {
                        cpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        isPasswordVisible = false;
                    } else {
                        cpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        isPasswordVisible = true;
                    }
                    // Keep the cursor at the end of the text
                    cpass.setSelection(cpass.getText().length());
                    return true;
                }
            }
            return false;
        });



        letsgo.setOnClickListener(view -> {
            Intent intent = new Intent(newacc.this, loginpage.class);
            startActivity(intent);
            finish();
        });


        submit.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String username = name.getText().toString();
        String useremail = email.getText().toString();
        String usernumber = number.getText().toString();
        String userpass = pass.getText().toString();
        String usercpass = cpass.getText().toString();

        // Validate input fields
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(useremail) || TextUtils.isEmpty(usernumber)
                || TextUtils.isEmpty(userpass) || TextUtils.isEmpty(usercpass)) {
            Toast.makeText(newacc.this, "Fill Everything", Toast.LENGTH_SHORT).show();
        } else if (!userpass.equals(usercpass)) {
            Toast.makeText(newacc.this, "Both Passwords are different", Toast.LENGTH_SHORT).show();
        } else if (userpass.length() < 6) {
            Toast.makeText(newacc.this, "Too Short Password", Toast.LENGTH_SHORT).show();
        } else {
            pd.setMessage("Please Wait");
            pd.show();

            // Register with email and password in Firebase Auth
            mAuth.createUserWithEmailAndPassword(useremail, userpass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Send email verification
                                firebaseUser.sendEmailVerification().addOnCompleteListener(verifyTask -> {
                                    if (verifyTask.isSuccessful()) {
                                        // Store user details in Realtime Database with email as primary key
                                        storeUserData(username, useremail, usernumber, firebaseUser.getUid());
                                    } else {
                                        pd.dismiss();
                                        Toast.makeText(newacc.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            pd.dismiss();
                            Toast.makeText(newacc.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void storeUserData(String username, String useremail, String usernumber, String userId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("UserID", userId);
        map.put("Number", usernumber);
        map.put("Name", username);
        map.put("Email", useremail);

        // Use email as the primary key
        root.child("USERS").child(useremail.replace(".", ",")).setValue(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        Toast.makeText(newacc.this, "Please verify your email.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(newacc.this, loginpage.class);
                        startActivity(intent);
                        finish();
                    } else {
                        pd.dismiss();
                        Toast.makeText(newacc.this, "Failed to store user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
