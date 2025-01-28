package com.yead.mybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import Model.TransactionModel;
import ViewHolder.TransactionViewHolder;
import Prevalent.Prevalent;

public class TransactionHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference transactionRef;
    private FirebaseRecyclerAdapter<TransactionModel, TransactionViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        recyclerView = findViewById(R.id.recycler_view_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String userEmailKey = Prevalent.CurrentOnlineUser.getEmail().replace(".", ",");
        transactionRef = FirebaseDatabase.getInstance().getReference("TRANSACTIONS").child(userEmailKey);

        displayTransactions();
    }

    private void displayTransactions() {
        FirebaseRecyclerOptions<TransactionModel> options = new FirebaseRecyclerOptions.Builder<TransactionModel>()
                .setQuery(transactionRef, TransactionModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<TransactionModel, TransactionViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TransactionViewHolder holder, int position, @NonNull TransactionModel model) {
                holder.bind(model);

                String transactionKey = getRef(holder.getBindingAdapterPosition()).getKey();
                if (transactionKey == null) {
                    Toast.makeText(TransactionHistory.this, "Transaction not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                holder.itemView.setOnClickListener(v -> {
                    showDeleteConfirmationDialog(transactionKey);
                });
            }

            @NonNull
            @Override
            public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
                return new TransactionViewHolder(view);
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
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteTransaction(transactionKey);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteTransaction(String transactionKey) {
        transactionRef.child(transactionKey).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(TransactionHistory.this, "Transaction deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TransactionHistory.this, "Failed to delete transaction", Toast.LENGTH_SHORT).show();
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
