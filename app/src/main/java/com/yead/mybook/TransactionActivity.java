package com.yead.mybook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class TransactionActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productPrice, bkashNumber, quantityText;
    private Button increaseButton, decreaseButton, payButton;
    private int quantity = 1;
    private double pricePerItem = 0.0;
    private String productID, imageUrl, name, bkash;

    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        productID = getIntent().getStringExtra("PID");

        if (productID == null) {
            Toast.makeText(this, "Product ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        productsRef = FirebaseDatabase.getInstance().getReference().child("PRODUCTS").child(productID);

        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        bkashNumber = findViewById(R.id.bkash_number);
        quantityText = findViewById(R.id.quantity_text);
        increaseButton = findViewById(R.id.increase_button);
        decreaseButton = findViewById(R.id.decrease_button);
        payButton = findViewById(R.id.pay_button);

        loadProductDetails();

        increaseButton.setOnClickListener(v -> {
            quantity++;
            updatePrice();
        });

        decreaseButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updatePrice();
            } else {
                Toast.makeText(this, "Quantity can't be less than 1", Toast.LENGTH_SHORT).show();
            }
        });

        payButton.setOnClickListener(v -> {
            Intent intent = new Intent(TransactionActivity.this, VerifyTransaction.class);
            intent.putExtra("PID", productID);
            intent.putExtra("PRODUCT_NAME", name);
            intent.putExtra("PRODUCT_IMAGE", imageUrl);
            intent.putExtra("PRODUCT_BKASH", bkash);
            intent.putExtra("AMOUNT_PAID", quantity * pricePerItem);
            startActivity(intent);
            finish();
        });
    }

    private void loadProductDetails() {
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    imageUrl = snapshot.child("IMAGE").getValue(String.class);
                    name = snapshot.child("PNAME").getValue(String.class);
                    String priceString = snapshot.child("PRICE").getValue(String.class);
                    bkash = snapshot.child("BKASH").getValue(String.class);

                    productName.setText(name != null ? name : "Product Name");

                    if (priceString != null) {
                        try {
                            pricePerItem = Double.parseDouble(priceString);
                        } catch (NumberFormatException e) {
                            pricePerItem = 0.0;
                        }
                    }
                    updatePrice();

                    bkashNumber.setText("Seller's Bkash: " + (bkash != null ? bkash : "N/A"));

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Picasso.get().load(imageUrl).into(productImage);
                    } else {
                        productImage.setImageResource(R.drawable.baseline_add_photo_alternate_24);
                    }
                } else {
                    Toast.makeText(TransactionActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TransactionActivity.this, "Failed to load product details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePrice() {
        quantityText.setText(String.valueOf(quantity));
        double totalPrice = quantity * pricePerItem;
        productPrice.setText("Price :" + totalPrice +" Taka");
    }
}
