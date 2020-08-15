package com.example.whatsappclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.MessageAdapter;
import com.example.whatsappclone.model.Message;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mChats;
    private RecyclerView.Adapter<MessageAdapter.MessageViewHolder> mChatsAdapter;
    private RecyclerView.LayoutManager mChatsLayoutManager;
    ArrayList<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        messages = new ArrayList<>();
        mChats = findViewById(R.id.messages);
        mChats.setNestedScrollingEnabled(false);
        mChats.setHasFixedSize(false);
        mChatsLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mChats.setLayoutManager(mChatsLayoutManager);
        mChatsAdapter = new MessageAdapter(messages);
        mChats.setAdapter(mChatsAdapter);
    }
}