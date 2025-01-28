package com.yead.mybook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Start extends AppCompatActivity {
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        pd = new ProgressDialog(this);

        // Add a delay to simulate a splash screen
        new Handler().postDelayed(() -> {
            // Redirect directly to the login page after the splash screen
            Intent intent = new Intent(Start.this, loginpage.class);
            startActivity(intent);
            finish();
        }, 1500);  // 1.5 seconds delay
    }
}
