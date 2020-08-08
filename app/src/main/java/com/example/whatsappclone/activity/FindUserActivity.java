package com.example.whatsappclone.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.UserListAdapter;
import com.example.whatsappclone.model.User;
import com.example.whatsappclone.utils.CountryToPhonePrefix;
import com.example.whatsappclone.utils.StringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindUserActivity extends AppCompatActivity {

    private RecyclerView.Adapter<UserListAdapter.UserListViewHolder> mUserListAdapter;
    private List<User> userList, contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        userList = new ArrayList<>();
        contactList = new ArrayList<>();
        initializeRecyclerView();
        getContactList();
    }

    private void getContactList() {
        String isoPrefix = getCountryISO();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null, null);

        if(phones != null) {
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phone = StringUtils.normalizePhone(phone);
                if(!(phone.charAt(0) == '+')) {
                    phone = isoPrefix + phone;
                }
                User user = new User(name, phone);
                contactList.add(user);
                getUserDetails(user);
            }
            phones.close();
        }
    }

    private void getUserDetails(User user) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = mUserDB.orderByChild("phone").equalTo(user.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String phone = "", name = "";
                    for(DataSnapshot child: snapshot.getChildren()) {
                        Object nameObj = child.child("name").getValue();
                        Object phoneObj = child.child("phone").getValue();
                        if(nameObj != null) {
                            name = nameObj.toString();
                        }
                        if(phoneObj != null) {
                            phone = phoneObj.toString();
                        }
                        User mUser = new User(phone, name);
                        if(name.equals(phone)) {
                            for(User user : contactList) {
                                if(user.getPhone().equals(mUser.getPhone())) {
                                    mUser.setName(user.getName());
                                }
                            }
                        }
                        userList.add(mUser);
                        mUserListAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getCountryISO() {
        String iso = "";
        getApplicationContext();
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso() != null && !telephonyManager.getNetworkCountryIso().isEmpty()) {
            iso = telephonyManager.getNetworkCountryIso();
        }
        return CountryToPhonePrefix.getPhone(iso);
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