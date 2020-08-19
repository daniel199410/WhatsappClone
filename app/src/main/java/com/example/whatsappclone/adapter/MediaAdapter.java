package com.example.whatsappclone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private ArrayList<String> mediaUris;
    private Context context;

    public MediaAdapter(Context context, ArrayList<String> mediaUris) {
        this.mediaUris = mediaUris;
        this.context = context;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, parent, false);
        return new MediaViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        Glide.with(context).load(Uri.parse(mediaUris.get(position))).into(holder.mImage);
    }

    @Override
    public int getItemCount() {
        return mediaUris.size();
    }

    public static class MediaViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.mediaImage);
        }
    }
}
