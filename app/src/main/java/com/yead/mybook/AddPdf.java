package com.yead.mybook;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddPdf extends AppCompatActivity {

    private Button selectPdfButton, uploadPdfButton;
    private TextView selectedFileName;
    private Uri pdfUri;
    private ProgressBar progressBar;
    private static final int PICK_PDF_REQUEST = 1;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pdf);

        // Initialize Firebase Storage and Database references
        storageRef = FirebaseStorage.getInstance().getReference("PRODUCTSPDF");
        databaseRef = FirebaseDatabase.getInstance().getReference("PRODUCTSPDF");

        // Initialize Views
        selectPdfButton = findViewById(R.id.select_pdf_button);
        uploadPdfButton = findViewById(R.id.upload_pdf_button);
        selectedFileName = findViewById(R.id.selected_file_name);
        progressBar = findViewById(R.id.upload_progress_bar);

        // Select PDF Button
        selectPdfButton.setOnClickListener(view -> selectPdf());

        // Upload PDF Button
        uploadPdfButton.setOnClickListener(view -> {
            if (pdfUri != null) {
                uploadPdf();
            } else {
                Toast.makeText(AddPdf.this, "Please select a PDF first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Open file picker for PDF selection
    private void selectPdf() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pdfUri = data.getData();  // Get the selected PDF file URI
            String fileName = getFileNameFromUri(pdfUri);
            selectedFileName.setText(fileName != null ? fileName : "File selected");
        }
    }

    // Helper method to get the file name from the Uri
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result != null ? result.lastIndexOf('/') : -1;
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    // Upload PDF to Firebase Storage and save info to Firebase Database
    private void uploadPdf() {
        progressBar.setVisibility(View.VISIBLE);
        uploadPdfButton.setEnabled(false);

        // Get file name and upload to Firebase Storage
        String fileName = getFileNameFromUri(pdfUri);
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(pdfUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Save file information to Firebase Database
                    HashMap<String, Object> pdfInfo = new HashMap<>();
                    pdfInfo.put("pdfName", fileName);
                    pdfInfo.put("pdfUrl", uri.toString());

                    databaseRef.push().setValue(pdfInfo).addOnSuccessListener(unused -> {
                        progressBar.setVisibility(View.GONE);
                        uploadPdfButton.setEnabled(true);
                        Toast.makeText(AddPdf.this, "PDF uploaded successfully", Toast.LENGTH_SHORT).show();

                        // Return to main activity after upload
                        startActivity(new Intent(AddPdf.this, Try1.class));
                        finish();
                    });
                }))
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    uploadPdfButton.setEnabled(true);
                    Toast.makeText(AddPdf.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
