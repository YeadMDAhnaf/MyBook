package com.yead.mybook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Model.PdfModel;
import ViewHolder.PdfViewHolder;

public class searchpdf extends AppCompatActivity {

    private DatabaseReference PdfRef;
    private RecyclerView recyclerView;
    private EditText searchBar;
    private FirebaseRecyclerAdapter<PdfModel, PdfViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchpdf);

        // Initialize Firebase reference and views
        PdfRef = FirebaseDatabase.getInstance().getReference().child("PRODUCTSPDF");
        searchBar = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.pdf_results_recycler_view);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load PDFs initially
        loadPdf("");

        // Set up search bar listener
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadPdf(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Method to load PDFs based on search query
    private void loadPdf(String searchQuery) {
        FirebaseRecyclerOptions<PdfModel> options = new FirebaseRecyclerOptions.Builder<PdfModel>()
                .setQuery(PdfRef, PdfModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<PdfModel, PdfViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PdfViewHolder holder, int position, @NonNull PdfModel model) {
                String pdfName = model.getPdfName().toLowerCase();

                // Display item if it matches search query or if search query is empty
                if (pdfName.contains(searchQuery) || searchQuery.isEmpty()) {
                    holder.itemView.setVisibility(View.VISIBLE);
                    holder.txtPdfName.setText(model.getPdfName());

                    // Click "View" button to open PDF in browser
                    holder.viewButton.setOnClickListener(view -> openPdfInBrowser(model.getPdfUrl()));
                } else {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }

            @NonNull
            @Override
            public PdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_item_layout, parent, false);
                return new PdfViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    // Method to open PDF URL in the browser
    private void openPdfInBrowser(String pdfUrl) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl));
        startActivity(browserIntent);
        Toast.makeText(this, "Opening PDF in browser...", Toast.LENGTH_SHORT).show();
    }
}