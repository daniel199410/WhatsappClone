package com.example.whatsappclone.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.MessageAdapter;
import com.example.whatsappclone.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    ArrayList<Message> messages;
    String chatId;
    DatabaseReference mChatDb;
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder> mChatsAdapter;
    RecyclerView.LayoutManager mChatsLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Button mSendButton = findViewById(R.id.sendButton);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            chatId = bundle.getString("chatId");
        }
        mChatDb = FirebaseDatabase.getInstance().getReference().child("chat").child(chatId);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        initializeRecyclerView();
        getChatMessages();
    }

    private void getChatMessages() {
        mChatDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    String message = "", creatorId = "";
                    Object messageDb = snapshot.child("text").getValue();
                    Object creatorDb = snapshot.child("creator").getValue();
                    if (messageDb != null) {
                        message = messageDb.toString();
                    }
                    if (creatorDb != null) {
                        creatorId = creatorDb.toString();
                    }
                    messages.add(new Message(snapshot.getKey(), message, creatorId));
                    mChatsLayoutManager.scrollToPosition(messages.size() - 1);
                    mChatsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage() {
        EditText mMessage = findViewById(R.id.messageInput);
        if (mMessage.getText() != null && !mMessage.getText().toString().isEmpty()) {
            DatabaseReference newMessageDb = mChatDb.push();
            Map<String, Object> newMessageMap = new HashMap<>();
            newMessageMap.put("text", mMessage.getText().toString());
            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());
            newMessageMap.put("createdAt", new Date());
            newMessageDb.updateChildren(newMessageMap);
            mMessage.setText(null);
        }
    }

    private void initializeRecyclerView() {
        messages = new ArrayList<>();
        RecyclerView mChats = findViewById(R.id.messages);
        mChats.setNestedScrollingEnabled(false);
        mChats.setHasFixedSize(false);
        mChatsLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mChats.setLayoutManager(mChatsLayoutManager);
        mChatsAdapter = new MessageAdapter(messages);
        mChats.setAdapter(mChatsAdapter);
    }
}