package com.finalproject.dogplay;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.finalproject.dogplay.adapters.PlaygroundsList;
import com.finalproject.dogplay.fragments.MapFragment;
import com.finalproject.dogplay.models.Playground;
import com.finalproject.dogplay.models.UserProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SearchDogParkActivity extends AppCompatActivity {

    private Fragment fragment;
    private ListView playgroundsListView;

    private List<Playground> playgrounds;
    private ArrayList<String> playgroundsStrList;
    private ArrayList<UserProfile> userProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dog_park);
        playgroundsListView = findViewById(R.id.playgrounds_listView);

        DatabaseReference databasePlaygrounds = FirebaseDatabase.getInstance().getReference().child("Playgrounds");
        playgrounds         = new ArrayList<>();
        playgroundsStrList  = new ArrayList<>();
        userProfiles        = new ArrayList<>();

        databasePlaygrounds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot playgroundSnapshot : dataSnapshot.getChildren()) {
                    Playground playground = makePlaygroundFromFB(playgroundSnapshot);
                    playgrounds.add(playground);
                    //final String playgroundToString = Objects.requireNonNull(playground).toString();

                    playgroundsStrList.add(stringForMap(playground));

                }
                setFragmentBundle();
                setListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }// end of onCreate

    private String stringForMap(Playground playground){
        return playground.getAddress() + "### " +  playground.getLatitude() + "### "
                + playground.getLongitude() + "### " + playground.getUsers().size();
    }

    private Playground makePlaygroundFromFB(DataSnapshot playgroundSnapshot){
        Playground playground = new Playground();
        userProfiles = new ArrayList<>();
        playground.setAddress((String) playgroundSnapshot.child("address").getValue());
        playground.setId(playgroundSnapshot.getKey());
        playground.setLatitude((double) playgroundSnapshot.child("latitude").getValue());
        playground.setLongitude((double) playgroundSnapshot.child("longitude").getValue());
        for (DataSnapshot dataSnapshot1 : playgroundSnapshot.child("visitors").getChildren())
            userProfiles.add(dataSnapshot1.child("userProfile").getValue(UserProfile.class));

        playground.setUsers(userProfiles);
        return playground;
    }

    private void setFragmentBundle() {

        Bundle mapFragBundle = new Bundle();

        if (!playgroundsStrList.isEmpty()) {

            mapFragBundle.putStringArrayList("EXTRA_PLAYGROUNDS", playgroundsStrList);
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (fragment != null) {
            ft.remove(fragment).commit();
        }
        fragment = new MapFragment();
        fragment.setArguments(mapFragBundle);
        ft.replace(R.id.playgrounds_mapview_fragment_container, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }

    private void setListView() {

        PlaygroundsList adapter = new PlaygroundsList(SearchDogParkActivity.this, playgrounds);
        playgroundsListView.setAdapter(adapter);

        playgroundsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchDogParkActivity.this, ViewPlaygroundActivity.class);
                intent.putExtra("EXTRA_SELECTED_PLAYGROUND", (playgrounds.get(position)).getAddress());
                intent.putExtra("EXTRA_PLAYGROUND_ID", (playgrounds.get(position)).getId());
                startActivity(intent);
            }
        });

    }

} // end of class
