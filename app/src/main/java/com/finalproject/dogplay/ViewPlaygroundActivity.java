package com.finalproject.dogplay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.finalproject.dogplay.models.Playground;
import com.finalproject.dogplay.models.UserProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewPlaygroundActivity extends AppCompatActivity {

    TextView                playgroundNameTextView;
    String                  playgroundName;
    Playground              currentPlayground;
    ListView                usersListView;
    ArrayList<UserProfile>  usersList;
    DatabaseReference       databasePlaygrounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_playground);

        databasePlaygrounds = FirebaseDatabase.getInstance().getReference("Playgrounds");

        playgroundName = getIntent().getStringExtra("EXTRA_SELECTED_PLAYGROUND");

        playgroundNameTextView = findViewById(R.id.playgroundName_textView);
        playgroundNameTextView.setText(playgroundName);

        usersListView = findViewById(R.id.users_listview);

        usersList = new ArrayList<>();

        databasePlaygrounds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot playgroundSnapshot : dataSnapshot.getChildren()) {
                    Playground playground = playgroundSnapshot.getValue(Playground.class);
                    if (playground.getAddress().equals(playgroundName)) {
                        currentPlayground = playground;
                        usersList.addAll(playground.getUsers());
                    }
                }
                setUsersListView(usersList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void setUsersListView(ArrayList<UserProfile> usersList) {
        UsersList adapter = new UsersList(ViewPlaygroundActivity.this, usersList);
        usersListView.setAdapter(adapter);
    }
}
