package com.yead.mybook;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import Model.Products;
import Prevalent.Prevalent;

public class mybooklist extends AppCompatActivity {

    private RecyclerView myBookList;
    private DatabaseReference ProductsRef;
    private FirebaseRecyclerAdapter<Products, BookViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybooklist);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("PRODUCTS");

        myBookList = findViewById(R.id.recycler_my_books);
        myBookList.setLayoutManager(new LinearLayoutManager(this));

        // Load books using the updated user's name from Prevalent
        loadMyBooks();
    }

    private void loadMyBooks() {
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                // Query using the updated user name
                .setQuery(ProductsRef.orderByChild("USER").equalTo(Prevalent.CurrentOnlineUser.getEmail()), Products.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Products, BookViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull Products model) {
                holder.txtBookName.setText(model.getPNAME());
                holder.txtBookPrice.setText(model.getPRICE());
                holder.txtBookDescription.setText(model.getDESCRIPTION());
                Picasso.get().load(model.getIMAGE()).into(holder.imageView);

                holder.imageView.setOnClickListener(view -> {
                    CharSequence options[] = new CharSequence[]{"Delete"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(mybooklist.this);
                    builder.setTitle("Book Options");

                    builder.setItems(options, (dialogInterface, i) -> {
                        if (i == 0) {
                            String selectedProductKey = getRef(holder.getBindingAdapterPosition()).getKey();
                            if (selectedProductKey != null) {
                                deleteBook(selectedProductKey);
                            } else {
                                Toast.makeText(mybooklist.this, "Item no longer exists.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();
                });
            }

            @NonNull
            @Override
            public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                return new BookViewHolder(view);
            }
        };

        myBookList.setAdapter(adapter);
        adapter.startListening();

        // Observe for changes and handle empty list
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkIfListIsEmpty();
            }
        });
    }

    private void deleteBook(String productKey) {
        ProductsRef.child(productKey).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(mybooklist.this, "Book deleted successfully", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged(); // Refresh the adapter
            } else {
                Toast.makeText(mybooklist.this, "Failed to delete the book", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfListIsEmpty() {
        if (adapter.getItemCount() == 0) {
            Toast.makeText(mybooklist.this, "No books available.", Toast.LENGTH_SHORT).show();
        }
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        public TextView txtBookName, txtBookPrice, txtBookDescription;
        public ImageView imageView;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            txtBookName = itemView.findViewById(R.id.product_name);
            txtBookPrice = itemView.findViewById(R.id.product_price);
            txtBookDescription = itemView.findViewById(R.id.product_description);
            imageView = itemView.findViewById(R.id.product_image);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
