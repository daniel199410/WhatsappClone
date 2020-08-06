package com.example.whatsappclone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.model.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    private List<User> userList;

    public UserListAdapter(List<User> userList) {
        this.userList = userList;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(layoutParams);
        return new UserListViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        holder.mName.setText(userList.get(position).getName());
        holder.mPhone.setText(userList.get(position).getPhone());
    }

    @Override
    public int getItemCount() {
        return this.userList.size();
    }

    public static class UserListViewHolder extends RecyclerView.ViewHolder {
        public TextView mName, mPhone;

        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mPhone = itemView.findViewById(R.id.phone);
        }
    }
}
