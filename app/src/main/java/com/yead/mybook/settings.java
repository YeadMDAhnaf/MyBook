package com.yead.mybook;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import Prevalent.Prevalent;
import de.hdodenhof.circleimageview.CircleImageView;

public class settings extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText nameEditText, numberEditText, addressEditText, payEditText;
    private Button update;
    private Uri imageUri;
    private TextView buni;
    private String myUrl = "";
    private StorageReference storageProfilePictureRef;
    private String checker = "";
    private TextView change;
    private StorageTask uploadTask;
    private ProgressDialog progressDialog;
    private TextView forget;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileImageView = findViewById(R.id.profile_image);
        nameEditText = findViewById(R.id.Name);
        numberEditText = findViewById(R.id.Number);
        addressEditText = findViewById(R.id.Address);
        update = findViewById(R.id.Update);
        change = findViewById(R.id.Change);
        buni = findViewById(R.id.Email);
        forget = findViewById(R.id.forgetpass);
        payEditText = findViewById(R.id.Bkash);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile_Pictures");

        progressDialog = new ProgressDialog(this);
        loadUserData();

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(settings.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        update.setOnClickListener(view -> {
            if (checker.equals("clicked")) {
                userInfoSaved();
            } else {
                updateOnlyUserInfo();
            }
        });

        change.setOnClickListener(view -> {
            checker = "clicked";
            openGallery();
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                Bitmap rotatedBitmap = rotateImageIfRequired(bitmap, imageUri);

                profileImageView.setImageBitmap(rotatedBitmap);
                imageUri = getImageUriFromBitmap(rotatedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error! Try Again!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error! Try Again!", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = getContentResolver().openInputStream(selectedImage);
        ExifInterface ei = new ExifInterface(input);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        File file = new File(getCacheDir(), "profile_image.jpg");
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())) {
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(addressEditText.getText().toString())) {
            Toast.makeText(this, "Address is mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(numberEditText.getText().toString())) {
            Toast.makeText(this, "Number is mandatory", Toast.LENGTH_SHORT).show();
        } else if (checker.equals("clicked")) {
            uploadImage();
        } else {
            updateOnlyUserInfo();
        }
    }

    private void uploadImage() {
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileRef = storageProfilePictureRef.child(Prevalent.CurrentOnlineUser.getNumber() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUrl = (Uri) task.getResult();
                    myUrl = downloadUrl.toString();
                    updateWithImage();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(settings.this, "Error: Unable to upload image", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWithImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("USERS");

        HashMap<String, Object> map = new HashMap<>();
        map.put("Name", nameEditText.getText().toString());
        map.put("Number", numberEditText.getText().toString());
        map.put("Address", addressEditText.getText().toString());
        map.put("Bkash", payEditText.getText().toString());
        map.put("Image", myUrl);

        reference.child(Prevalent.CurrentOnlineUser.getEmail().replace(".", ",")).updateChildren(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(settings.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                        Prevalent.CurrentOnlineUser.setImage(myUrl);
                        redirectToMyAccount();
                    } else {
                        Toast.makeText(settings.this, "Error updating profile.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateOnlyUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("USERS");

        HashMap<String, Object> map = new HashMap<>();
        map.put("Name", nameEditText.getText().toString());
        map.put("Number", numberEditText.getText().toString());
        map.put("Address", addressEditText.getText().toString());
        map.put("Bkash", payEditText.getText().toString());

        reference.child(Prevalent.CurrentOnlineUser.getEmail().replace(".", ",")).updateChildren(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(settings.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                        redirectToMyAccount();
                    } else {
                        Toast.makeText(settings.this, "Error updating profile.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserData() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(Prevalent.CurrentOnlineUser.getEmail().replace(".", ","));

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("Image").exists()) {
                        String image = snapshot.child("Image").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                    }

                    String name = snapshot.child("Name").getValue(String.class);
                    String number = snapshot.child("Number").getValue(String.class);
                    String address = snapshot.child("Address").getValue(String.class);
                    String email = snapshot.child("Email").getValue(String.class);
                    String bkash = snapshot.child("Bkash").getValue(String.class); // Load Bkash if available

                    nameEditText.setText(name);
                    numberEditText.setText(number);
                    addressEditText.setText(address);
                    buni.setText(email);

                    // Set the Bkash field if it exists
                    if (bkash != null) {
                        payEditText.setText(bkash);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(settings.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToMyAccount() {
        Intent intent = new Intent(settings.this, Try1.class);
        startActivity(intent);
        finish();
    }
}
