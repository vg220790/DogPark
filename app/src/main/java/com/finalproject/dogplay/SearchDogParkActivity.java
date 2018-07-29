package com.finalproject.dogplay;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchDogParkActivity extends AppCompatActivity {

    Fragment fragment;
    Bundle mapFragBundle;
    DatabaseReference databasePlaygrounds;
    ArrayList<String> playgrounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dog_park);

        databasePlaygrounds = FirebaseDatabase.getInstance().getReference("Playgrounds");
        playgrounds = new ArrayList<String>();

        setFragmentBundle();

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


    }// end of onCreate

    public  void setFragmentBundle(){
        mapFragBundle = new Bundle();

        databasePlaygrounds.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot profilesSnapshot: dataSnapshot.getChildren()){
                    Playground playground = profilesSnapshot.getValue(Playground.class);
                    playgrounds.add(playground.toString());
                }
                mapFragBundle.putStringArrayList("EXTRA_PLAYGROUNDS",playgrounds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //get from firebase all playgrounds and their users
        //put in bundle String Array of all data in this format: (Playground1:)"address lat lang [userProfile,userProfile,userProfile]";address lat lang [userProfile]"
    }

} // end of class
