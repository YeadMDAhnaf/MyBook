package com.yead.mybook;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import Model.Products;
import Prevalent.Prevalent;
import ViewHolder.ProductViewHolder;
import de.hdodenhof.circleimageview.CircleImageView;

public class Try1 extends AppCompatActivity {

    private ImageView settings1, upload1, logout1, pdf1, book,mes;
    private CircleImageView profile_image1;
    private EditText searchBar;
    private RecyclerView recyclerView;
    private DatabaseReference ProductRef;
    private FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter;
    private String chuni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try1);

        // Initialize Firebase reference and views
        ProductRef = FirebaseDatabase.getInstance().getReference().child("PRODUCTS");
        settings1 = findViewById(R.id.settings);
        upload1 = findViewById(R.id.upload);
        logout1 = findViewById(R.id.logout);
        pdf1 = findViewById(R.id.searchpdf);
        profile_image1 = findViewById(R.id.profile_image);
        searchBar = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.recycle);
        book = findViewById(R.id.booklist);
        mes = findViewById(R.id.message);
        chuni = getIntent().getStringExtra("Address");

        // Set up RecyclerView with GridLayoutManager
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 columns
        recyclerView.setLayoutManager(gridLayoutManager);

        // Set click listeners for navigation buttons
        book.setOnClickListener(view -> startActivity(new Intent(Try1.this, Notification.class)));
        settings1.setOnClickListener(view -> startActivity(new Intent(Try1.this, settings.class)));
        upload1.setOnClickListener(view -> startActivity(new Intent(Try1.this, AdminAddNewProduct.class)));
        logout1.setOnClickListener(view -> startActivity(new Intent(Try1.this, loginpage.class)));
        pdf1.setOnClickListener(view -> startActivity(new Intent(Try1.this, searchpdf.class)));

        profile_image1.setOnClickListener(view -> {
            Intent intent = new Intent(Try1.this, TransactionHistory.class);
            startActivity(intent);
        });
        mes.setOnClickListener(view -> {
            Intent intent = new Intent(Try1.this,SearchMessage.class);
            startActivity(intent);
        });

        // Set up search bar listener
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchProducts(charSequence.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        updateUserInfo(); // Load user info and profile image
        searchProducts(""); // Load all products initially
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load the profile image if URL is available
        String imageUrl = Prevalent.CurrentOnlineUser.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(profile_image1);
        } else {
            profile_image1.setImageResource(R.drawable.profile);
        }
    }

    // Method to update user information and profile image
    private void updateUserInfo() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("USERS").child(Prevalent.CurrentOnlineUser.getEmail().replace(".", ","));

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String image = snapshot.child("Image").getValue(String.class);
                    if (image != null && !image.isEmpty()) {
                        Picasso.get().load(image).into(profile_image1);
                    } else {
                        profile_image1.setImageResource(R.drawable.profile);
                    }
                    Prevalent.CurrentOnlineUser.setImage(image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors here if needed
            }
        });
    }

    // Method to search and display products based on substring input
    private void searchProducts(String query) {
        // Retrieve all products (limit to 100 for performance)
        Query firebaseSearchQuery = ProductRef.limitToFirst(100);

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(firebaseSearchQuery, Products.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                String productName = model.getPNAME().toLowerCase();
                String searchQuery = query.toLowerCase();

                // Check if the product name contains the search query
                if (productName.contains(searchQuery) || searchQuery.isEmpty()) {
                    // Display the item if it matches the search query or if the search query is empty
                    holder.itemView.setVisibility(View.VISIBLE);
                    holder.txtProductName.setText(model.getPNAME());
                    holder.txtProductDescription.setText(model.getDESCRIPTION());
                    holder.txtProductPrice.setText("Price: " + model.getPRICE());
                    Picasso.get().load(model.getIMAGE()).into(holder.imageView);

                    // Click listener to open product details
                    holder.itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(Try1.this, ProductDetailsActivity.class);
                        intent.putExtra("PID", model.getPID());
                        intent.putExtra("Address",chuni);
                        startActivity(intent);
                    });
                } else {
                    // Hide the item if it doesn't match the search query
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                return new ProductViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to exit the app?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

}
