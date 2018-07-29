package com.finalproject.dogplay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    DatabaseReference databaseUserProfiles;
    UserProfile currentUserProfile;
    String uname = "";
    String dname = "";

    Button userDataBtn, accountSetBtn, findPlayground;
    TextView username, dogname, doginfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /////////


        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        userDataBtn = (Button)findViewById(R.id.update_user_data);
        accountSetBtn = (Button)findViewById(R.id.update_account_settings);
        findPlayground = (Button)findViewById(R.id.findPlayground);

        username = (TextView) findViewById(R.id.username);
        dogname = (TextView) findViewById(R.id.dogname);
        doginfo = (TextView) findViewById(R.id.dogInfo);

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        databaseUserProfiles = FirebaseDatabase.getInstance().getReference("UserProfiles");

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        getCurrentUserProfile(user);

        userDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userProfileIntent = new Intent(MainActivity.this, UserProfileActivity.class);
                uname = currentUserProfile.getuName();
                dname = currentUserProfile.getdName();
                Bundle extras = new Bundle();
                extras.putString("EXTRA_USERNAME",uname);
                extras.putString("EXTRA_DOGNAME",dname);
                extras.putStringArrayList("EXTRA_DOGATTRIBUTES",currentUserProfile.getdDescription());
                userProfileIntent.putExtras(extras);
                startActivity(userProfileIntent);

                //startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
            }
        });

        accountSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AccountSettingsActivity.class));
            }
        });

        findPlayground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchDogParkActivity.class));
            }
        });

    }//end of onCreate

    public void getCurrentUserProfile(FirebaseUser user){
        final String current_userID = user.getUid();
        databaseUserProfiles.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot profilesSnapshot: dataSnapshot.getChildren()){
                    UserProfile userProfile = profilesSnapshot.getValue(UserProfile.class);
                    if (userProfile.getuID().equals(current_userID))
                        currentUserProfile = userProfile;
                }
                if (currentUserProfile == null){
                    //start UserProfile Activity
                    startActivity(new Intent(MainActivity.this, FirstUserProfileActivity.class));
                }else{
                    username.setText(currentUserProfile.getuName());
                    dogname.setText(currentUserProfile.getdName());
                    showDogDescription();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void showDogDescription(){
        //firstly getting mandatory attribute of dog's size
        String dogInfo = "size: " + currentUserProfile.getdDescription().get(0) + "\n";

        for (String attribute: currentUserProfile.getdDescription().subList(1,(currentUserProfile.getdDescription().size()))){
            dogInfo += "\n" + attribute;
        }
        doginfo.setText(dogInfo);
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

}//end of class
