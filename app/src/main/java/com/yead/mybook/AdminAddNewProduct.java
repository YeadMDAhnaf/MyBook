package com.yead.mybook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Prevalent.Prevalent;

public class AdminAddNewProduct extends AppCompatActivity {

    private String Description, Price, Pname, saveCurrentDate, saveCurrentTime, productRandomKey, downloadImageUrl;
    private Button AddNewProductButton, addPDFButton;
    private EditText InputProductName, InputProductDescription, InputProductPrice;
    private ImageView InputProductImage;
    private static final int GALLERY_PICK = 1;
    private Uri imageUri;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;
    private String buni,chuni;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
  DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("USERS");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        AddNewProductButton = findViewById(R.id.Add);
        InputProductImage = findViewById(R.id.image);
        InputProductName = findViewById(R.id.productname);
        InputProductDescription = findViewById(R.id.description);
        InputProductPrice = findViewById(R.id.Price);
        addPDFButton = findViewById(R.id.addPDF);
        buni = currentUser.getEmail();
        data.orderByChild("Email").equalTo(buni.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ssnapshot : snapshot.getChildren()) {

                        chuni = ssnapshot.child("Address").getValue().toString();

                        //Toast.makeText(SearchMessage.this, queryEmail + " " + productEmail, Toast.LENGTH_SHORT).show();


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("PRODUCTS");

        loadingBar = new ProgressDialog(this);

        // Open PDF add activity
        addPDFButton.setOnClickListener(view -> {
            Intent intent = new Intent(AdminAddNewProduct.this, AddPdf.class);
            startActivity(intent);
        });

        // Open gallery to select image
        InputProductImage.setOnClickListener(view -> openGallery());

        // Validate data and store it when button is clicked
        AddNewProductButton.setOnClickListener(view -> validateProductData());
    }

    // Open the gallery for image selection
    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            InputProductImage.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show();
        }
    }

    // Validate product data before uploading
    private void validateProductData() {
        Description = InputProductDescription.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString();

        if (imageUri == null) {
            Toast.makeText(this, "Product image is required.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Description)) {
            Toast.makeText(this, "Please provide a product description.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Pname)) {
            Toast.makeText(this, "Please provide a product name.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Please provide a product price.", Toast.LENGTH_SHORT).show();
        } else {
            storeProductInformation();
        }
    }

    // Store product information in Firebase
    private void storeProductInformation() {
        loadingBar.setTitle("Adding Product");
        loadingBar.setMessage("Please wait, while we are adding the product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;
        final StorageReference filePath = ProductImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(e -> {
            Toast.makeText(AdminAddNewProduct.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }).addOnSuccessListener(taskSnapshot -> {
            filePath.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    downloadImageUrl = task.getResult().toString();
                    saveProductInfoToDatabase();
                }
            });
        });
    }

    private void saveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        String productName = Pname.toLowerCase();
        productMap.put("PID", productRandomKey);
        productMap.put("DATE", saveCurrentDate);
        productMap.put("TIME", saveCurrentTime);
        productMap.put("DESCRIPTION", Description);
        productMap.put("IMAGE", downloadImageUrl);
        productMap.put("PNAME", Pname);
        productMap.put("PRICE", Price);
        productMap.put("PNAME_LOWER", productName);
        productMap.put("USER", Prevalent.CurrentOnlineUser.getEmail());
        productMap.put("BKASH", Prevalent.CurrentOnlineUser.getBkash());
        productMap.put("Address", chuni);

        ProductsRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadingBar.dismiss();
                Toast.makeText(AdminAddNewProduct.this, "Product added successfully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminAddNewProduct.this, Try1.class);
                intent.putExtra("Address",chuni);
                startActivity(intent);
                finish();
            } else {
                loadingBar.dismiss();
                String message = task.getException().toString();
                Toast.makeText(AdminAddNewProduct.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });


    }
}
