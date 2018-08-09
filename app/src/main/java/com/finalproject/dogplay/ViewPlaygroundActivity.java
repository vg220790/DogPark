package com.finalproject.dogplay;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;


public class ViewPlaygroundActivity extends AppCompatActivity implements ActivityCallback{

    private String                  playgroundName;
    private Playground              currentPlayground;
    private ListView                usersListView;
    private ArrayList<String>       usersList;
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

        openChat();


        databasePlaygrounds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot playgroundSnapshot : dataSnapshot.getChildren()) {
                    Playground playground = playgroundSnapshot.getValue(Playground.class);
                    if (Objects.requireNonNull(playgroundSnapshot.getKey()).equals(playgroundName)) {
                        currentPlayground = playground;
                        ArrayList<String> visitors = new ArrayList();
                        Iterator<DataSnapshot> playgroundSnapshotIterator = playgroundSnapshot.child("visitors").getChildren().iterator();
                        while (playgroundSnapshotIterator.hasNext())
                            visitors.add((String)playgroundSnapshotIterator.next().child("id").getValue());
                        playground.setVisitors(visitors);
                        Log.d("visit", usersList.toString());
                        usersList.addAll(visitors);
                    }
                }
                setUsersListView(getUsersFromId(usersList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    protected ArrayList<UserProfile> getUsersFromId(final ArrayList<String> userIds){
        userProfiles = new ArrayList<>();
        DatabaseReference databasePlaygrounds = FirebaseDatabase.getInstance().getReference("UserProfiles");
        databasePlaygrounds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if(userIds.contains(userSnapshot.child("uID").getValue())) {
                        userProfiles.add(userSnapshot.getValue(UserProfile.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return userProfiles;
    }

    protected void setUsersListView(ArrayList<UserProfile> usersList) {
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


