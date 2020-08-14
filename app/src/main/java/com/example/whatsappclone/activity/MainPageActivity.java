package com.example.whatsappclone.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.ChatListAdapter;
import com.example.whatsappclone.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainPageActivity extends AppCompatActivity {

    RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> mChatListAdapter;
    private List<Chat> chatList;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Button mLogout = findViewById(R.id.logout);
        Button mFindUser = findViewById(R.id.findUser);
        chatList = new ArrayList<>();
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        mFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FindUserActivity.class));
            }
        });
        getPermissions();
        initializeRecyclerView();
        getUserChats();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermissions() {
        requestPermissions(new String[]{
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_CONTACTS
        }, 1);
    }

    private void initializeRecyclerView() {
        RecyclerView mChatList = findViewById(R.id.chatList);
        mChatList.setNestedScrollingEnabled(false);
        mChatList.setHasFixedSize(false);
        RecyclerView.LayoutManager mChatListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mChatList.setLayoutManager(mChatListLayoutManager);
        mChatListAdapter = new ChatListAdapter(chatList);
        mChatList.setAdapter(mChatListAdapter);
    }

    private void getUserChats() {
        String uid = FirebaseAuth.getInstance().getUid();
        if(uid != null) {
            DatabaseReference mUserChatDB = FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("chat");
            mUserChatDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for(DataSnapshot child : snapshot.getChildren()) {
                            chatList.add(new Chat(child.getKey()));
                            mChatListAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}