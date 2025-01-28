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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Users;
import Prevalent.Prevalent;

public class loginpage extends AppCompatActivity {
    private EditText number, pass;
    private Button login, create, forget;
    private ProgressDialog pd;
    private DatabaseReference root;
    private FirebaseAuth mAuth;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);

        number = findViewById(R.id.number);
        pass = findViewById(R.id.password);
        login = findViewById(R.id.login);
        create = findViewById(R.id.newacc);
        forget = findViewById(R.id.forgot);
        pd = new ProgressDialog(this);
        root = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Toggle password visibility on the eye icon
        pass.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (pass.getRight() - pass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    // Toggle password visibility
                    if (isPasswordVisible) {
                        pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        isPasswordVisible = false;
                    } else {
                        pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        isPasswordVisible = true;
                    }
                    // Keep the cursor at the end of the text
                    pass.setSelection(pass.getText().length());
                    return true;
                }
            }
            return false;
        });

        forget.setOnClickListener(view -> {
            Intent intent = new Intent(loginpage.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        create.setOnClickListener(view -> {
            Intent intent = new Intent(loginpage.this, newacc.class);
            startActivity(intent);
            finish();
        });

        login.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String useremail = number.getText().toString().trim();
        String userpass = pass.getText().toString().trim();

        if (TextUtils.isEmpty(useremail) || TextUtils.isEmpty(userpass)) {
            Toast.makeText(loginpage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            pd.setMessage("Logging in...");
            pd.show();
            authenticateUserByEmail(useremail, userpass);
        }
    }

    private void authenticateUserByEmail(String email, String userpass) {
        mAuth.signInWithEmailAndPassword(email, userpass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                            checkUserInDatabase(email, userpass);
                        } else {
                            pd.dismiss();
                            Toast.makeText(loginpage.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        pd.dismiss();
                        Toast.makeText(loginpage.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserInDatabase(String email, String userpass) {
        String sanitizedEmail = email.replace(".", ",");

        root.child("USERS").child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users userdata = snapshot.getValue(Users.class);

                    if (userdata != null) {
                        pd.dismiss();
                        Toast.makeText(loginpage.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                        Prevalent.CurrentOnlineUser = userdata;
                        Intent intent = new Intent(loginpage.this, Try1.class);
                        startActivity(intent);
                        finish();
                    } else {
                        pd.dismiss();
                        Toast.makeText(loginpage.this, "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(loginpage.this, "Account with this email does not exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(loginpage.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
