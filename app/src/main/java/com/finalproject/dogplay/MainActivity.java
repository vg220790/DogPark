package com.finalproject.dogplay;

import android.Manifest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActivityManager;

import com.finalproject.dogplay.user.AccountSettingsActivity;
import com.finalproject.dogplay.user.FirstUserProfileActivity;
import com.finalproject.dogplay.user.LoginActivity;
import com.finalproject.dogplay.user.UserProfileActivity;
import com.finalproject.dogplay.models.UserProfile;
import com.finalproject.dogplay.service.BackgroundService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseUserProfiles;
    private UserProfile currentUserProfile;

    static String user_name, dog_name;
    ArrayList<String> dog_description;


    private TextView username, dogName, dogInfo;

    private BroadcastReceiver broadcastReceiver;
    private Location userLocation;

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    userLocation = (Location) Objects.requireNonNull(intent.getExtras()).get("location");
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        //////
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        //////
        handleUserLogin();

        ProgressBar progressBar = findViewById(R.id.progressBar);
        Button userDataBtn      = findViewById(R.id.update_user_data);
        Button accountSetBtn    = findViewById(R.id.update_account_settings);
        Button findPlayground   = findViewById(R.id.findPlayground);
        username                = findViewById(R.id.username);
        dogName                 = findViewById(R.id.dogname);
        dogInfo                 = findViewById(R.id.dogInfo);

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

    private void handleUserLogin(){
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        //get firebase auth instance
        auth                    = FirebaseAuth.getInstance();
        databaseUserProfiles    = FirebaseDatabase.getInstance().getReference("UserProfiles");

        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //wait for user info
            }
        }, 400);
    }


    private void getCurrentUserProfile(FirebaseUser user){
        final String current_userID = user.getUid();
        databaseUserProfiles.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot profilesSnapshot: dataSnapshot.getChildren()){
                    UserProfile userProfile = profilesSnapshot.getValue(UserProfile.class);
                    if (Objects.requireNonNull(userProfile).getuID().equals(current_userID)) {
                        currentUserProfile = userProfile;
                        user_name = userProfile.getuName();
                    }

                }
                if (currentUserProfile == null) //start UserProfile Activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                else
                    showData();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showData(){
        user_name = currentUserProfile.getuName();
        dog_name = currentUserProfile.getdName();
        if(user_name.length() > 10 )
            username.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        if(dogName.length() > 10 )
            dogName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        username.setText(user_name);
        dogName.setText(dog_name);
        OneSignal.sendTag("user_email", currentUserProfile.getuEMail());
        showDogDescription();
    }

    private void showDogDescription(){
        //firstly getting mandatory attribute of dog's size
        StringBuilder dogsInfo = new StringBuilder("Dogs size: " + currentUserProfile.getdDescription().get(0));
        for (String attribute: currentUserProfile.getdDescription().subList(1,(currentUserProfile.getdDescription().size())))
            dogsInfo.append("\n").append(attribute);
        this.dogInfo.setText(dogsInfo.toString());
        dog_description = currentUserProfile.getdDescription();
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    private void GPSService(){
        /*start gps service*/
        if(!isRunningService("com.finalproject.dogplay.service.BackgroundService")) {
            if(!runtime_permissions()){
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
    private boolean isRunningService(String serviceName){
        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : Objects.requireNonNull(manager).getRunningServices(Integer.MAX_VALUE)){
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

    public static void sendNotification(final String follower_email) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic Y2MwMThjZjUtOTZiYS00ZTgyLWI0ZGEtZmNiYzFmYmU3YTc1");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"7817bdf4-af44-444d-a472-49c5c068b600\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"user_email\", \"relation\": \"=\", \"value\": \"" + follower_email + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"English Message\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }


}//end of class
