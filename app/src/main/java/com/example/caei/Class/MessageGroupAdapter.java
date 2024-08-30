package com.example.caei.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caei.R;

import java.util.ArrayList;

public class MessageGroupAdapter extends RecyclerView.Adapter<MessageGroupAdapter.MessageGroupViewHolder> {

    private Context context;
    private ArrayList<MessageGroup> messages;
    private int currentUserId; // Add this to identify the current user

    public MessageGroupAdapter(Context context, ArrayList<MessageGroup> messages, int currentUserId) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId; // Initialize currentUserId
    }

    @NonNull
    @Override
    public MessageGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageGroupViewHolder holder, int position) {
        MessageGroup messageGroup = messages.get(position);

        holder.textViewSender.setText(String.valueOf(messageGroup.getSenderId())); // You might want to map senderId to a sender name if you have one
        holder.textViewMessage.setText(messageGroup.getMessage());
        holder.textViewTimestamp.setText(messageGroup.getTimestamp());

        // Align the message based on sender
        if (messageGroup.getSenderId() == currentUserId) {
            // Align message to the right
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.cardMessage.getLayoutParams();
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET;
            holder.cardMessage.setLayoutParams(layoutParams);
            holder.textViewSender.setVisibility(View.GONE); // Hide sender ID for right-aligned messages
        } else {
            // Align message to the left
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.cardMessage.getLayoutParams();
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            holder.cardMessage.setLayoutParams(layoutParams);
            holder.textViewSender.setVisibility(View.VISIBLE); // Show sender ID for left-aligned messages
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageGroupViewHolder extends RecyclerView.ViewHolder {

        TextView textViewSender, textViewMessage, textViewTimestamp;
        CardView cardMessage;

        public MessageGroupViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewSender = itemView.findViewById(R.id.textViewSender);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
            cardMessage = itemView.findViewById(R.id.card_message);
        }
    }
}
