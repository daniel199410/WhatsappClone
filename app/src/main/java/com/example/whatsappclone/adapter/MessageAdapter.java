package com.example.whatsappclone.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.model.Message;
import com.google.firebase.auth.FirebaseAuth;
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
        if (messages.get(position).getMediaUrls().isEmpty()) {
            holder.mViewMedia.setVisibility(View.GONE);
        } else {
            Glide.with(holder.mContext).load(Uri.parse(messages.get(position).getMediaUrls().get(0))).into(holder.mViewMedia);
            holder.mViewMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ImageViewer.Builder<>(view.getContext(), messages.get(position).getMediaUrls())
                            .setStartPosition(0)
                            .show();
                }
            });
        }
        String senderId = messages.get(position).getSenderId();
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null && uid.equals(senderId)) {
            holder.mLinearLayout.setGravity(Gravity.END);
        } else {
            holder.mLinearLayout.setGravity(Gravity.START);
            holder.mItemMessageContainerLayout.setBackgroundColor(holder.mResources.getColor(R.color.colorDivider));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView mMessage;
        private ImageButton mViewMedia;
        private LinearLayout mLinearLayout, mItemMessageContainerLayout;
        private Resources mResources;
        private Context mContext;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            mMessage = itemView.findViewById(R.id.message);
            mResources = itemView.getResources();
            mViewMedia = itemView.findViewById(R.id.imageMessage);
            mLinearLayout = itemView.findViewById(R.id.itemMessageLayout);
            mItemMessageContainerLayout = itemView.findViewById(R.id.itemMessageContainerLayout);
            mContext = itemView.getContext();
        }
    }
}
