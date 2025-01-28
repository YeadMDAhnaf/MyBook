package com.yead.mybook;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import Model.Products;

public class ProductDetailsActivity extends AppCompatActivity {
    private Button add_to_cart,bunedi;
    private ImageView product_image;
    private TextView product_description, product_price, product_name;
    private String productId = "",productEmail = "", userEmail,chuni;
    DatabaseReference data ;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_details);
        data = FirebaseDatabase.getInstance().getReference().child("USERS");

        productId = getIntent().getStringExtra("PID");

        add_to_cart = findViewById(R.id.Add);
        product_image = findViewById(R.id.image);
        product_name = findViewById(R.id.product_name_details);
        product_price = findViewById(R.id.product_price);
        product_description = findViewById(R.id.product_description);
        userEmail = currentUser.getEmail();
        chuni = getIntent().getStringExtra("Address");
        bunedi = findViewById(R.id.messagebuni);

        get_product_details(productId);

        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this, TransactionActivity.class);
                intent.putExtra("PID", productId);  // Pass the product ID to TransactionActivity
                startActivity(intent);
            }
        });
        bunedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this,ChatBox.class);
                intent.putExtra("ProductEmail", productEmail);
                intent.putExtra("UserEmail", userEmail);
                startActivity(intent);
            }
        });
    }

    private void get_product_details(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("PRODUCTS").child(productId);
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Products products = snapshot.getValue(Products.class);
                    if (products != null) {
                        productEmail = snapshot.child("USER").getValue().toString();
                        product_name.setText(products.getPNAME());
                        product_price.setText(products.getPRICE());
                        product_description.setText(products.getDESCRIPTION());
                        Picasso.get().load(products.getIMAGE()).into(product_image);
                    }
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailsActivity.this, "Failed to load product details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
