package com.finalproject.dogplay;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
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
    UserProfile currentUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_view_playground);

        playgroundName = getIntent().getStringExtra("EXTRA_SELECTED_PLAYGROUND");
        String playgroundId = getIntent().getStringExtra("EXTRA_PLAYGROUND_ID");

        TextView playgroundNameTextView = findViewById(R.id.playgroundName_textView);
        playgroundNameTextView.setText(playgroundName);

        usersListView = findViewById(R.id.users_listview);

        usersList = new ArrayList<>();

        DatabaseReference databasePlaygroundsUsers = FirebaseDatabase.getInstance().getReference("Playgrounds").child(playgroundId).child("visitors");
        databasePlaygroundsUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot playgroundSnapshot) {
                Playground playground = playgroundSnapshot.getValue(Playground.class);
                usersList = new ArrayList<>();
                userProfiles = new ArrayList();
                for (DataSnapshot dataSnapshot1 : playgroundSnapshot.getChildren())
                    userProfiles.add(dataSnapshot1.child("userProfile").getValue(UserProfile.class));
                if(!userProfiles.isEmpty())
                    playground.setUsers(userProfiles);
                Log.d("visit", userProfiles.toString());
                usersList.addAll(userProfiles);
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
        final String current_userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final ArrayList<UserProfile> list = usersList;
        DatabaseReference databasePlaygroundsUsers = FirebaseDatabase.getInstance().getReference("UserProfiles");
        databasePlaygroundsUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot profilesSnapshot : dataSnapshot.getChildren()) {
                    UserProfile userProfile = profilesSnapshot.getValue(UserProfile.class);
                    if (Objects.requireNonNull(userProfile).getuID().equals(current_userID)) {
                        currentUserProfile = userProfile;
                        UsersList adapter = new UsersList(ViewPlaygroundActivity.this, list, currentUserProfile);
                        usersListView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //do nothing
            }
        }, 400);

//        UsersList adapter = new UsersList(ViewPlaygroundActivity.this, usersList, currentUserProfile);
//        usersListView.setAdapter(adapter);
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


