package com.finalproject.dogplay;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dog_park);
        playgroundsListView = findViewById(R.id.playgrounds_listView);

        DatabaseReference databasePlaygrounds = FirebaseDatabase.getInstance().getReference().child("Playgrounds");
        playgrounds = new ArrayList<>();
        playgroundsStrList = new ArrayList<>();

        databasePlaygrounds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot playgroundSnapshot : dataSnapshot.getChildren()) {
                    Playground playground = playgroundSnapshot.getValue(Playground.class);
                    ArrayList<String> visitors = new ArrayList();
                    for (DataSnapshot dataSnapshot1 : playgroundSnapshot.child("visitors").getChildren())
                        visitors.add((String) dataSnapshot1.child("id").getValue());
                    playground.setVisitors(visitors);
                    playgrounds.add(playground);
                    playgroundsStrList.add(Objects.requireNonNull(playground).toString());
                }
                setFragmentBundle();
                setListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }// end of onCreate

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
