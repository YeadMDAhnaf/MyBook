package com.yead.mybook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchMessage extends AppCompatActivity {
    private EditText search;
    private TextView dis;
    private Button searchButton;
    private CircleImageView profileImage1;
    private String productEmail, UserEmail,buni,chuni;
    DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("USERS");
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_message);

        // Initialize UI components
        search = findViewById(R.id.search);
        dis = findViewById(R.id.display);
        searchButton = findViewById(R.id.search_button);
        UserEmail = currentUser.getEmail();
        profileImage1 = findViewById(R.id.profileImage);

        // Set search button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchButtonClick(view);
            }
        });
    }


    public void onSearchButtonClick(View view) {
        String queryEmail = search.getText().toString().trim();
        if (!queryEmail.isEmpty()) {
            data.orderByChild("Name").equalTo(queryEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ssnapshot : snapshot.getChildren()) {

                                productEmail = ssnapshot.child("Email").getValue().toString();
                                buni = ssnapshot.child("Name").getValue().toString();
                                chuni = ssnapshot.child("Image").getValue().toString();


                        }
                        dis.setText(buni);
                        Picasso.get().load(chuni).into(profileImage1);
                        dis.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(SearchMessage.this, ChatBox.class);
                                intent.putExtra("ProductEmail", productEmail);
                                intent.putExtra("UserEmail", UserEmail);
                                intent.putExtra("Name",buni);
                                startActivity(intent);
                            }
                        });
                    } else {
                        //Toast.makeText(SearchMessage.this, queryEmail + " " + productEmail, Toast.LENGTH_SHORT).show();
                        dis.setText("No user found with this email.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            dis.setText("Please enter an email to search.");
        }
    }
}
