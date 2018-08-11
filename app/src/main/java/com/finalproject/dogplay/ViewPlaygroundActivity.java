package com.finalproject.dogplay;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.finalproject.dogplay.adapters.UsersList;
import com.finalproject.dogplay.fragments.ChatFragment;
import com.finalproject.dogplay.models.Playground;
import com.finalproject.dogplay.models.UserProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class ViewPlaygroundActivity extends AppCompatActivity implements ActivityCallback{

    private String                  playgroundName;
    private ListView                usersListView;
    private ArrayList<UserProfile>  usersList;
    private ArrayList<UserProfile>  userProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_playground);

        DatabaseReference databasePlaygrounds = FirebaseDatabase.getInstance().getReference("Playgrounds");

        playgroundName = getIntent().getStringExtra("EXTRA_SELECTED_PLAYGROUND");

        TextView playgroundNameTextView = findViewById(R.id.playgroundName_textView);
        playgroundNameTextView.setText(playgroundName);

        usersListView = findViewById(R.id.users_listview);

        usersList = new ArrayList<>();

        databasePlaygrounds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot playgroundSnapshot : dataSnapshot.getChildren()) {
                    Playground playground = playgroundSnapshot.getValue(Playground.class);
                    if (Objects.requireNonNull(playgroundSnapshot.child("address").getValue()).equals(playgroundName)) {
                        userProfiles = new ArrayList();
                        for (DataSnapshot dataSnapshot1 : playgroundSnapshot.child("visitors").getChildren())
                            userProfiles.add(dataSnapshot1.child("userProfile").getValue(UserProfile.class));
                        playground.setUsers(userProfiles);
                        Log.d("visit", userProfiles.toString());
                        usersList.addAll(userProfiles);
                    }
                }
                setUsersListView(usersList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openChat();
            }
        }, 400);

    }


    private void setUsersListView(ArrayList<UserProfile> usersList) {
        UsersList adapter = new UsersList(ViewPlaygroundActivity.this, usersList);
        usersListView.setAdapter(adapter);
    }


    @Override
    public void openChat() {
        replaceFragment(ChatFragment.newInstance());
    }

    /// Private methods

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.chat_fragment, fragment)
                .commit();
    }

    public String getCurrentPlaygroundID() {
        return playgroundName = getIntent().getStringExtra("EXTRA_PLAYGROUND_ID");
    }
}


