package com.yead.mybook;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private ArrayList<Message> messagesList;
    private String ownEmail;
    private String searchingEmail;

    // Constructor to initialize the messages list and emails
    public MessageAdapter(ArrayList<Message> messagesList, String ownEmail, String searchingEmail) {
        this.messagesList = messagesList;
        this.ownEmail = ownEmail;
        this.searchingEmail = searchingEmail;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messagesList.get(position);

        // Set message content
        holder.messageText.setText(message.getMessageContent());
        holder.messageText2.setText(message.getMessageContent());
        holder.timestamp.setText(message.getTimestamp());
        holder.timestamp2.setText(message.getTimestamp());

        // Check if the message was sent by the own user or the searching user
        if (message.getSenderEmail().equals(ownEmail)) {
            // If the sender is the own user, align the message to the right and timestamp to the left
            holder.messageText.setBackgroundResource(R.drawable.message_background); // Set custom background for own message
            holder.messageText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END); // Align message text to the end (right)
            holder.timestamp.setVisibility(View.GONE); // Initially hide the timestamp for own messages
            holder.timestamp.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START); // Align timestamp text to the start (left)
            holder.timestamp.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black)); // Optional: Set timestamp color for own messages

            // Hide message2 and timestamp2 for own messages
            holder.messageText2.setVisibility(View.GONE);
            holder.timestamp2.setVisibility(View.GONE);

            // Add click listener to messageText to toggle timestamp visibility
            holder.messageText.setOnClickListener(v -> {
                if (holder.timestamp.getVisibility() == View.VISIBLE) {
                    holder.timestamp.setVisibility(View.GONE);
                } else {
                    holder.timestamp.setVisibility(View.VISIBLE);
                }
            });
        } else if (message.getSenderEmail().equals(searchingEmail)) {
            // If the sender is the searching user, align the message to the left and timestamp to the right
            holder.messageText2.setBackgroundResource(R.drawable.message_background_recieved); // Set custom background for other user's message
            holder.messageText2.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black)); // Set text color for received messages
            holder.messageText2.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START); // Align message text to the start (left)
            holder.timestamp2.setVisibility(View.GONE); // Initially hide the timestamp for received messages
            holder.timestamp2.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END); // Align timestamp text to the end (right)
            holder.timestamp2.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black)); // Optional: Set timestamp color for received messages

            // Hide message and timestamp for the own messages
            holder.messageText.setVisibility(View.GONE);
            holder.timestamp.setVisibility(View.GONE);

            // Add click listener to messageText2 to toggle timestamp2 visibility
            holder.messageText2.setOnClickListener(v -> {
                if (holder.timestamp2.getVisibility() == View.VISIBLE) {
                    holder.timestamp2.setVisibility(View.GONE);
                } else {
                    holder.timestamp2.setVisibility(View.VISIBLE);
                }
            });
        }
    }



    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timestamp;
        TextView messageText2;
        TextView timestamp2;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestamp = itemView.findViewById(R.id.timestamp);
            messageText2 = itemView.findViewById(R.id.messageText2);
            timestamp2 = itemView.findViewById(R.id.timestamp2);
        }
    }
}