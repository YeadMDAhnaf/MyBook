package com.yead.mybook;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import Model.Products;
import ViewHolder.ProductViewHolder;

public class SearchActivity extends AppCompatActivity {

    private DatabaseReference ProductRef;
    private RecyclerView recyclerView;
    private EditText searchBar;
    FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ProductRef = FirebaseDatabase.getInstance().getReference().child("PRODUCTS");

        recyclerView = findViewById(R.id.search_results_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchBar = findViewById(R.id.search_bar);

        // Adding a TextWatcher for the search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Call searchProducts whenever the text changes
                searchProducts(charSequence.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        searchProducts(""); // Display all products initially
    }

    // Method to search products based on the input, supporting substring matches
    private void searchProducts(String query) {
        Query firebaseSearchQuery;

        if (query.isEmpty()) {
            // If no query, show all products
            firebaseSearchQuery = ProductRef.orderByChild("PRICE"); // Sort by price if no search query
        } else {
            // Perform a search using the PNAME_LOWER field with substring matching
            firebaseSearchQuery = ProductRef.orderByChild("PNAME_LOWER")
                    .startAt(query.toLowerCase())
                    .endAt(query.toLowerCase() + "\uf8ff"); // This handles substring matching
        }

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(firebaseSearchQuery, Products.class)
                        .build();

        // Set up the FirebaseRecyclerAdapter
        adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                // Bind data to the views
                holder.txtProductName.setText(model.getPNAME());
                holder.txtProductDescription.setText(model.getDESCRIPTION());
                holder.txtProductPrice.setText(model.getPRICE());
                Picasso.get().load(model.getIMAGE()).into(holder.imageView);

                // Set a click listener to open product details
                holder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(SearchActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("PID", model.getPID());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                return new ProductViewHolder(view);
            }
        };

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
