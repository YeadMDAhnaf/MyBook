package com.yead.mybook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import Prevalent.Prevalent;
import Model.TransactionModel;

public class finalTransaction extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productBkash, userBkash, paidAmount;
    private Button returnHomeButton;
    private static final String TAG = "FinalTransactionActivity";
    private static final String ADMIN_EMAIL = "mybook9231@gmail.com";
    private static final String ADMIN_PASSWORD = "mlxz lgyb cqao oyqu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_transaction);

        // Initialize views
        productImage = findViewById(R.id.image);
        productName = findViewById(R.id.product_name);
        productBkash = findViewById(R.id.product_bkash);
        userBkash = findViewById(R.id.user_bkash);
        paidAmount = findViewById(R.id.paid_amount);
        returnHomeButton = findViewById(R.id.return_home_button);

        // Retrieve product details from intent
        Intent intent = getIntent();
        String productID = intent.getStringExtra("PID");
        String name = intent.getStringExtra("PRODUCT_NAME");
        String imageUrl = intent.getStringExtra("PRODUCT_IMAGE");
        String productBkashNumber = intent.getStringExtra("PRODUCT_BKASH");
        double amountPaid = intent.getDoubleExtra("AMOUNT_PAID", 0.0);

        // Display product details
        productName.setText(name != null ? name : "Product Name");
        productBkash.setText("Seller's Bkash: " + (productBkashNumber != null ? productBkashNumber : "N/A"));
        paidAmount.setText("Paid Amount: " + amountPaid + " taka");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(productImage);
        } else {
            productImage.setImageResource(R.drawable.baseline_add_photo_alternate_24);
        }

        loadUserBkash();

        // Save transaction to Firebase
        saveTransaction(productID, name, productBkashNumber, amountPaid);

        returnHomeButton.setOnClickListener(v -> {
            startActivity(new Intent(finalTransaction.this, Try1.class));
            finish();
        });
    }

    private void loadUserBkash() {
        String userEmailKey = Prevalent.CurrentOnlineUser.getEmail().replace(".", ",");

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("USERS").child(userEmailKey);

        userRef.child("Bkash").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String userBkashNumber = task.getResult().getValue(String.class);
                userBkash.setText("Your Bkash: " + (userBkashNumber != null ? userBkashNumber : "N/A"));

            } else {
                Toast.makeText(finalTransaction.this, "Failed to load user Bkash number.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTransaction(String productId, String productName, String productBkash, double amountPaid) {
        String userEmailKey = Prevalent.CurrentOnlineUser.getEmail().replace(".", ",");
        DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference("TRANSACTIONS").child(userEmailKey);

        TransactionModel transaction = new TransactionModel(productId, productName, productBkash, amountPaid);
        String transactionKey = transactionRef.push().getKey();

        if (transactionKey != null) {
            transactionRef.child(transactionKey).setValue(transaction).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to save transaction: " + task.getException());
                    Toast.makeText(this, "Failed to save transaction", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}