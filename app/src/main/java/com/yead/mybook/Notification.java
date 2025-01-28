package com.yead.mybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Model.NotificationModel;
import Model.TransactionModel;
import Prevalent.Prevalent;
import ViewHolder.NotificationViewHolder;
import ViewHolder.TransactionViewHolder;

public class Notification extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference transactionRef;
    private FirebaseRecyclerAdapter<NotificationModel, NotificationViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification2);
        recyclerView = findViewById(R.id.recycler_view_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String userEmailKey = Prevalent.CurrentOnlineUser.getEmail().replace(".", ",");
        transactionRef = FirebaseDatabase.getInstance().getReference("Notification").child(userEmailKey);

        displayTransactions();

    }
    private void displayTransactions() {
        FirebaseRecyclerOptions<NotificationModel> options = new FirebaseRecyclerOptions.Builder<NotificationModel>()
                .setQuery(transactionRef, NotificationModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<NotificationModel, NotificationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int position, @NonNull NotificationModel model) {
                holder.bind(model);

                String transactionKey = getRef(holder.getBindingAdapterPosition()).getKey();
                if (transactionKey == null) {
                    Toast.makeText(Notification.this, "Transaction not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                holder.itemView.setOnClickListener(v -> {
                    showDeleteConfirmationDialog(transactionKey);
                });
            }

            @NonNull
            @Override
            public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
                return new NotificationViewHolder(view);
            }

            @Override
            public int getItemCount() {
                int count = super.getItemCount();
                return count;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void showDeleteConfirmationDialog(String transactionKey) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Notification")
                .setMessage("Are you sure you want to delete this Notification?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteTransaction(transactionKey);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteTransaction(String transactionKey) {
        transactionRef.child(transactionKey).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Notification.this, "Transaction deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Notification.this, "Failed to delete transaction", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}