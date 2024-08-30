package com.example.caei.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caei.Messages;
import com.example.caei.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_CURRENT_USER = 1;
    private static final int VIEW_TYPE_OTHER_USER = 2;

    private Context context;
    private ArrayList<Message> messages;
    private int currentUserId;

    public MessageAdapter(Context context, ArrayList<Message> messages, int currentUserId) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_CURRENT_USER) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_other_user, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.textViewSender.setText(message.getSenderName());
        holder.textViewMessage.setText(message.getMessage());
        holder.textViewTimestamp.setText(message.getTimestamp());

        // Set layout parameters based on sender
        if (message.getSenderId() == currentUserId) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.cardMessage.getLayoutParams();
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET;
            holder.cardMessage.setLayoutParams(layoutParams);
            holder.textViewSender.setVisibility(View.GONE);
        } else {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.cardMessage.getLayoutParams();
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            holder.cardMessage.setLayoutParams(layoutParams);
            holder.textViewSender.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return message.getSenderId() == currentUserId ? VIEW_TYPE_CURRENT_USER : VIEW_TYPE_OTHER_USER;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSender, textViewMessage, textViewTimestamp;
        CardView cardMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSender = itemView.findViewById(R.id.textViewSender);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
            cardMessage = itemView.findViewById(R.id.card_message);
        }
    }
}
