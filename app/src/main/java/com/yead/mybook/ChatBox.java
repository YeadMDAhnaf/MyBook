package com.yead.mybook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Model.Notification;
import Prevalent.Prevalent;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatBox extends AppCompatActivity {

    private RecyclerView ownMessagesRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageView buni;
    private TextView ss;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messagesList = new ArrayList<>();
    private CircleImageView profile_image1;

    private DatabaseReference databaseReference, databaseReference2;
    private String ownEmail, searchingEmail,chuni,rani,bunbuni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);

        // Retrieve emails from Intent
        Intent intent = getIntent();
        ownEmail = intent.getStringExtra("UserEmail");
        searchingEmail = intent.getStringExtra("ProductEmail");
        chuni = intent.getStringExtra("Name");
        rani = intent.getStringExtra("Used");

        // Initialize views
        buni = findViewById(R.id.backButton);
        profile_image1 = findViewById(R.id.profileImage);
        ss = findViewById(R.id.sda);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        ownMessagesRecyclerView = findViewById(R.id.ownMessagesRecyclerView);

        // Set up RecyclerView
        ownMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter for both own and other user's messages
        messageAdapter = new MessageAdapter(messagesList, ownEmail, searchingEmail);
        ownMessagesRecyclerView.setAdapter(messageAdapter);

        loadname();

        // Load recent messages
        loadRecentMessages();

        // Send button action
        sendButton.setOnClickListener(v -> sendMessage());

        buni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatBox.this, SearchMessage.class);
                startActivity(intent);
                finish();
            }
        });
    }



    private void loadname() {
       DatabaseReference dat =  FirebaseDatabase.getInstance().getReference().child("USERS");
       dat.orderByChild("Email").equalTo(searchingEmail).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   for(DataSnapshot ssnapshot : snapshot.getChildren()){
                       chuni = ssnapshot.child("Name").getValue().toString();
                       rani = ssnapshot.child("Image").getValue().toString();
                   }
                   ss.setText(chuni);
                   Picasso.get().load(rani).into(profile_image1);
               }
           }
           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });



    }

    private void loadRecentMessages() {
        // Initialize database references
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats")
                .child(encodeEmail(ownEmail)).child(encodeEmail(searchingEmail));
        databaseReference2 = FirebaseDatabase.getInstance().getReference("Chats")
                .child(encodeEmail(searchingEmail)).child(encodeEmail(ownEmail));

        // Query for own messages
        databaseReference.limitToLast(50).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                String messageContent = snapshot.child("messageContent").getValue(String.class);
                String senderEmail = snapshot.child("senderEmail").getValue(String.class);
                String timestamp = snapshot.child("timestamp").getValue(String.class);
                String messageId = snapshot.getKey();  // Get the message ID

                if (messageContent != null && senderEmail != null && timestamp != null) {
                    // Check if message ID already exists in the list
                    boolean messageExists = false;
                    for (Message message : messagesList) {
                        if (messageId.equals(message.getMessageId())) {
                            messageExists = true;
                            break;
                        }
                    }

                    // Add message if it does not exist in the list
                    if (!messageExists) {
                        Message message = new Message(senderEmail, messageContent, timestamp, messageId);
                        messagesList.add(message);  // Add to the unified list
                        messageAdapter.notifyItemInserted(messagesList.size() - 1);
                        ownMessagesRecyclerView.scrollToPosition(messagesList.size() - 1);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Handle message changes if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle message removal if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Handle message move if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatBox.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
            }
        });

        // Query for other user's messages
        databaseReference2.limitToLast(50).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                String messageContent = snapshot.child("messageContent").getValue(String.class);
                String senderEmail = snapshot.child("senderEmail").getValue(String.class);
                String timestamp = snapshot.child("timestamp").getValue(String.class);
                String messageId = snapshot.getKey();  // Get the message ID

                if (messageContent != null && senderEmail != null && timestamp != null) {
                    // Check if message ID already exists in the list
                    boolean messageExists = false;
                    for (Message message : messagesList) {
                        if (messageId.equals(message.getMessageId())) {
                            messageExists = true;
                            break;
                        }
                    }

                    // Add message if it does not exist in the list
                    if (!messageExists) {
                        Message message = new Message(senderEmail, messageContent, timestamp, messageId);
                        messagesList.add(message);  // Add to the unified list
                        messageAdapter.notifyItemInserted(messagesList.size() - 1);
                        ownMessagesRecyclerView.scrollToPosition(messagesList.size() - 1);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Handle message changes if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle message removal if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Handle message move if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatBox.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        if (messageInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        sendnoti(searchingEmail);

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats")
                .child(encodeEmail(ownEmail)).child(encodeEmail(searchingEmail));
        databaseReference2 = FirebaseDatabase.getInstance().getReference("Chats")
                .child(encodeEmail(searchingEmail)).child(encodeEmail(ownEmail));

        String messageText = messageInput.getText().toString().trim();
        String timestamp = new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date());
        Message message = new Message(ownEmail, messageText, timestamp, null);  // No message ID yet

        String messageId = databaseReference.push().getKey();
        if (messageId != null) {
            message.setMessageId(messageId);  // Set message ID after generating it
            databaseReference.child(messageId).setValue(message);
            databaseReference2.child(messageId).setValue(message);
            // Clear message input
            messageInput.setText("");

        } else {
            Toast.makeText(this, "Failed to send message.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendnoti(String searchingEmail) {
        DatabaseReference dat = FirebaseDatabase.getInstance().getReference().child("USERS");
        dat.orderByChild("Email").equalTo(ownEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ssnapshot : snapshot.getChildren()) {
                        bunbuni = ssnapshot.child("Name").getValue(String.class); // Get sender's name
                    }
                }

                // Proceed to create and push the notification
                DatabaseReference datu = FirebaseDatabase.getInstance().getReference("Notification").child(encodeEmail(searchingEmail));
                Notification noti = new Notification(bunbuni, chuni); // bunbuni = sender's name, chuni = receiver's name

                // Generate a unique key for the notification
                String buuni = datu.push().getKey();
                if (buuni != null) {
                    datu.child(buuni).setValue(noti).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChatBox.this, "Notification sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChatBox.this, "Failed to send notification.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatBox.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Helper method to encode email for Firebase database path
    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }
}