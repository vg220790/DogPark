package com.finalproject.dogplay;

import android.Manifest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.location.Location;
import android.os.Bundle;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActivityManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.finalproject.dogplay.fragments.ChatFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivityCallback{

    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    DatabaseReference databaseUserProfiles;
    UserProfile currentUserProfile;


    Button userDataBtn, accountSetBtn, findPlayground;
    TextView username, dogname, doginfo;

    private BroadcastReceiver broadcastReceiver;
    private Location userLocation;

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    userLocation = (Location) intent.getExtras().get("location");
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /////////


        progressBar     = findViewById(R.id.progressBar);
        userDataBtn     = findViewById(R.id.update_user_data);
        accountSetBtn   = findViewById(R.id.update_account_settings);
        findPlayground  = findViewById(R.id.findPlayground);
        username        = findViewById(R.id.username);
        dogname         = findViewById(R.id.dogname);
        doginfo         = findViewById(R.id.dogInfo);

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        //get firebase auth instance
        auth                    = FirebaseAuth.getInstance();
        databaseUserProfiles    = FirebaseDatabase.getInstance().getReference("UserProfiles");

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


        GPSService();

        userDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userProfileIntent = new Intent(MainActivity.this, UserProfileActivity.class);
                Bundle extras = new Bundle();
                extras.putString("EXTRA_USERNAME",currentUserProfile.getuName());
                extras.putString("EXTRA_DOGNAME",currentUserProfile.getdName());
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


    @Override
    public void openChat() {
        replaceFragment(ChatFragment.newInstance());
    }


    /// Private methods

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

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
                    //openChat();
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

    public void GPSService(){
        /*start gps service*/
        if(!isRunningService("com.finalproject.dogplay.BackgroundService")) {
            if(runtime_permissions()){}
            else{
                Intent i =new Intent(getApplicationContext(),BackgroundService.class);
                startService(i);
                Toast.makeText(MainActivity.this, "service started!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(MainActivity.this, "service has already started!", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Method the checks if service is running
     * @param serviceName String representing the service name
     * @return If the service is running or not
     */
    public boolean isRunningService(String serviceName){
        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceName.equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Intent i =new Intent(getApplicationContext(),BackgroundService.class);
                startService(i);
                Toast.makeText(MainActivity.this, "Background Service start", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public UserProfile getCurrentUserProfile(){
        return currentUserProfile;
    }


}//end of class
