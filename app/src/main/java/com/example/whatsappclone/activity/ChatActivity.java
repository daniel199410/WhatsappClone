package com.example.whatsappclone.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.MediaAdapter;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUris;
    ArrayList<Message> messages;
    String chatId;
    DatabaseReference mChatDb;
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder> mChatsAdapter;
    RecyclerView.Adapter<MediaAdapter.MediaViewHolder> mMediaAdapter;
    RecyclerView.LayoutManager mChatsLayoutManager, mRecyclerViewLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Button mSendButton = findViewById(R.id.sendButton);
        Button mAddMedia = findViewById(R.id.addMedia);
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
        mAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        initializeMessagesRecyclerView();
        initializeMediaRecyclerView();
        getChatMessages();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select pictures"), PICK_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == PICK_IMAGE_INTENT) {
                if(data != null) {
                    if(data.getClipData() == null) {
                        if(data.getData() != null) {
                            mediaUris.add(data.getData().toString());
                            mMediaAdapter.notifyDataSetChanged();
                        }
                    } else {
                        for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                            mediaUris.add(data.getClipData().getItemAt(i).toString());
                            mMediaAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
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

    private void initializeMessagesRecyclerView() {
        messages = new ArrayList<>();
        RecyclerView mChats = findViewById(R.id.messages);
        mChats.setNestedScrollingEnabled(false);
        mChats.setHasFixedSize(false);
        mChatsLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mChats.setLayoutManager(mChatsLayoutManager);
        mChatsAdapter = new MessageAdapter(messages);
        mChats.setAdapter(mChatsAdapter);
    }

    private void initializeMediaRecyclerView() {
        mediaUris = new ArrayList<>();
        RecyclerView mMedia = findViewById(R.id.mediaList);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(false);
        mRecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mMedia.setLayoutManager(mRecyclerViewLayoutManager);
        mMediaAdapter = new MediaAdapter(getApplicationContext(), mediaUris);
        mMedia.setAdapter(mMediaAdapter);
    }
}