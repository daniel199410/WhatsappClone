package com.example.whatsappclone.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.MediaAdapter;
import com.example.whatsappclone.adapter.MessageAdapter;
import com.example.whatsappclone.model.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    EditText mMessage;

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
                            mediaUris.add(data.getClipData().getItemAt(i).getUri().toString());
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
                    ArrayList<String> mediaUrls = new ArrayList<>();
                    Object messageDb = snapshot.child("text").getValue();
                    Object creatorDb = snapshot.child("creator").getValue();
                    DataSnapshot mediaSnapshot = snapshot.child("media");
                    if (messageDb != null) {
                        message = messageDb.toString();
                    }
                    if (creatorDb != null) {
                        creatorId = creatorDb.toString();
                    }
                    if (mediaSnapshot.getChildrenCount() > 0) {
                        Object mediaValue;
                        for (DataSnapshot mediaChild : mediaSnapshot.getChildren()) {
                            mediaValue = mediaChild.getValue();
                            if (mediaValue != null) {
                                mediaUrls.add(mediaValue.toString());
                            }
                        }
                    }
                    messages.add(new Message(snapshot.getKey(), message, creatorId, mediaUrls));
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

    int totalMediaUploaded = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();
    private void sendMessage() {
    mMessage = findViewById(R.id.messageInput);
        String messageId = mChatDb.push().getKey();
        if(messageId != null) {
            final DatabaseReference newMessageDb = mChatDb.child(messageId);
            final Map<String, Object> newMessageMap = new HashMap<>();
            if(!mMessage.getText().toString().isEmpty()) {
                newMessageMap.put("text", mMessage.getText().toString());
            }
            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());
            newMessageMap.put("createdAt", new Date());
            if(!mediaUris.isEmpty()) {
                for(String mediaUri: mediaUris) {
                    String mediaId = newMessageDb.child("media").push().getKey();
                    if(mediaId != null) {
                        mediaIdList.add(mediaId);
                        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat").child(chatId).child(messageId).child(mediaId);
                        UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        newMessageMap.put("/media/" + mediaIdList.get(totalMediaUploaded) + "/", uri.toString());
                                        totalMediaUploaded++;
                                        if(totalMediaUploaded == mediaIdList.size()) {
                                            updateDataBaseWithNewMessage(newMessageDb, newMessageMap);
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            } else {
                if(!mMessage.getText().toString().isEmpty()) {
                    updateDataBaseWithNewMessage(newMessageDb, newMessageMap);
                }
            }
        }
    }

    private void updateDataBaseWithNewMessage(DatabaseReference newMessageDb, Map<String, Object> newMessageMap) {
        newMessageDb.updateChildren(newMessageMap);
        mMessage.setText(null);
        mediaUris.clear();
        mediaIdList.clear();
        mMediaAdapter.notifyDataSetChanged();
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