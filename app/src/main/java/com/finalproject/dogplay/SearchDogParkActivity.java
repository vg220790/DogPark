package com.finalproject.dogplay;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchDogParkActivity extends AppCompatActivity {

    Fragment fragment;
    Bundle mapFragBundle;
    ListView pg_listview;

    DatabaseReference databasePlaygrounds;
    List<Playground> playgrounds;
    ArrayList<String> playgrounds_strList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dog_park);
        pg_listview = (ListView) findViewById(R.id.playgrounds_listView);

        databasePlaygrounds = FirebaseDatabase.getInstance().getReference("Playgrounds");
        playgrounds = new ArrayList<Playground>();
        playgrounds_strList = new ArrayList<String>();

        databasePlaygrounds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot playgroundSnapshot : dataSnapshot.getChildren()) {
                    Playground playground = playgroundSnapshot.getValue(Playground.class);
                    playgrounds.add(playground);
                    playgrounds_strList.add(playground.toString());
                }
                setFragmentBundle();
                setListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }// end of onCreate

    public void setFragmentBundle() {

        mapFragBundle = new Bundle();

        if (!playgrounds_strList.isEmpty()) {

            mapFragBundle.putStringArrayList("EXTRA_PLAYGROUNDS", playgrounds_strList);
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

    public void setListView() {

        PlaygroundsList adapter = new PlaygroundsList(SearchDogParkActivity.this, playgrounds);
        pg_listview.setAdapter(adapter);

        pg_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected_pg_name = (playgrounds.get(position)).getAddress();
                Intent intent = new Intent(SearchDogParkActivity.this, ViewPlaygroundActivity.class);
                intent.putExtra("EXTRA_SELECTED_PLAYGROUND", selected_pg_name);
                startActivity(intent);
            }
        });

    }

} // end of class
