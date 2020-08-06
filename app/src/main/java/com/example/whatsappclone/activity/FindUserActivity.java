package com.example.whatsappclone.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.UserListAdapter;
import com.example.whatsappclone.model.User;

import java.util.ArrayList;
import java.util.List;

public class FindUserActivity extends AppCompatActivity {

    private RecyclerView.Adapter<UserListAdapter.UserListViewHolder> mUserListAdapter;
    private List<User> userList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        userList = new ArrayList<>();
        initializeRecyclerView();
        getContactList();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getContactList() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null);
        assert phones != null;
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            User user = new User(name, phone);
            userList.add(user);
            mUserListAdapter.notifyDataSetChanged();
        }
        phones.close();
    }

    private void initializeRecyclerView() {
        RecyclerView mUserList = findViewById(R.id.userList);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        RecyclerView.LayoutManager mUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);
        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);
    }
}