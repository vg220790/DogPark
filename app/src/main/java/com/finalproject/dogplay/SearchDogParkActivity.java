package com.finalproject.dogplay;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import java.util.List;


public class SearchDogParkActivity extends AppCompatActivity {

    private Fragment fragment;
    private ListView playgroundsListView;

    private List<Playground> playgrounds;
    private ArrayList<String> playgroundsStrList;
    private ArrayList<UserProfile> userProfiles;

    private boolean inActivity;

    @Override
    protected void onResume() {
        super.onResume();
        inActivity = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_search_dog_park);
        playgroundsListView = findViewById(R.id.playgrounds_listView);

        inActivity = true;

        DatabaseReference databasePlaygrounds = FirebaseDatabase.getInstance().getReference().child("Playgrounds");

        databasePlaygrounds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(inActivity) {
                    playgrounds = new ArrayList<>();
                    playgroundsStrList = new ArrayList<>();
                    for (DataSnapshot playgroundSnapshot : dataSnapshot.getChildren()) {
                        Playground playground = makePlaygroundFromFB(playgroundSnapshot);
                        playgrounds.add(playground);
                        playgroundsStrList.add(stringForMap(playground));

                    }
                    setListView();
                    setFragmentBundle();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //throw databaseError.toException();
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
        fragment = new MapFragment();
        fragment.setArguments(mapFragBundle);
        openFragment(fragment);
    }

    private void openFragment(final Fragment fragment)   {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.playgrounds_mapview_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

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
                inActivity = false;
                finish();

            }
        });


    }

} // end of class
