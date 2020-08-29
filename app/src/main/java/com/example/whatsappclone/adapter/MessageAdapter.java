package com.example.whatsappclone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.model.Message;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;

    public MessageAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(layoutParams);
        return new MessageViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, final int position) {
        holder.mMessage.setText(messages.get(position).getMessage());
        holder.mSender.setText(messages.get(position).getSenderId());
        if (messages.get(position).getMediaUrls().isEmpty()) {
            holder.mViewMedia.setVisibility(View.GONE);
        }
        holder.mViewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ImageViewer.Builder<>(view.getContext(), messages.get(position).getMediaUrls())
                        .setStartPosition(0)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView mMessage, mSender;
        private Button mViewMedia;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            mMessage = itemView.findViewById(R.id.message);
            mSender = itemView.findViewById(R.id.sender);
            mViewMedia = itemView.findViewById(R.id.viewMedia);
        }
    }
}
