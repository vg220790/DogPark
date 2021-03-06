package com.finalproject.dogplay.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.finalproject.dogplay.models.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class BackgroundService extends Service {

    private DatabaseReference playgrounds;

    private static final String TAG = "BackgroundService";
    private boolean isRunning;

    private double lat;
    private double lon;

    private LocationManager mLocationManager = null;
    //this is the time interval we used every ten seconds
    //for  testing purposes we made it to be every 10 seconds
    // this is supposed to be every 5 minutes 5*60*1000
    private static final int LOCATION_INTERVAL = 10*1000;
    //this is the distance interval we used for testing
    //this is supposed to be every 15 meters
    private static final float LOCATION_DISTANCE = 0;
    private LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private DatabaseReference userProfileRef;
    private UserProfile myUserProfile;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"service created");
        Context context = this;
        this.isRunning = false;

        //background service will not work (app will crash) if firebase user logged out
        loadUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeLocationManager();
            }
        }, 2500);


    }

    private void loadUser(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String mUserId = user.getUid();
        userProfileRef= FirebaseDatabase.getInstance().getReference().child("UserProfiles");
        userProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren())
                {
                    if(mUserId.equals(userSnapshot.child("uID").getValue())) {
                        myUserProfile = userSnapshot.getValue(UserProfile.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private Location getLocation(){
        Location location = null;
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
            if (mLocationManager != null)
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
        return location;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"start command");
        if(!this.isRunning){
            this.isRunning = true;

        }
        return START_STICKY;
    }




    @Override
    public void onDestroy() {

        this.isRunning = false;
        mangeUsers(0,0);
        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
        Log.i(TAG,"service destroyed");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        private LocationListener(String provider)
        {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {   //each time when the location changes we will notify our main activity
            mLastLocation.set(location);
            lat = location.getLatitude();
            lon = location.getLongitude();
            Log.i(TAG,"Location Changed: " + lat + " " + lon);

            mangeUsers(lat, lon);
            Intent i = new Intent("location_update");
            i.putExtra("location",location);
            sendBroadcast(i);

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderDisabled(String provider)
        {
        }

        @Override
        public void onProviderEnabled(String provider)
        {
        }

    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            Location location = getLocation();
            Log.i(TAG,"Location: " + location);
        }
    }

    private boolean inRange(double pLat, double pLon, double myLat, double myLon){

        return (haversine(pLat, pLon, myLat, myLon) * 1000) <= 35;

    }


    private double haversine(double lat1, double lng1, double lat2, double lng2) {
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        DecimalFormat df = new DecimalFormat("#.##");
        String dx = df.format(r * c);
        return Double.valueOf(dx);


    }

    private void mangeUsers(final double lat , final double lon){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String mUserId = user.getUid();

        playgrounds = FirebaseDatabase.getInstance().getReference().child("Playgrounds");
        playgrounds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot playgroundSnapshot : dataSnapshot.getChildren()) {

                    String playgroundName = (String) playgroundSnapshot.child("address").getValue();

                    double currentPlaygroundLat  = (Double) playgroundSnapshot.child("latitude").getValue();
                    double currentPlaygroundLon  = (Double) playgroundSnapshot.child("longitude").getValue();

                    boolean userIsInsideThePlayground = playgroundSnapshot.child("visitors").toString().contains("uID="+mUserId);

                    boolean inRange = inRange(currentPlaygroundLat, currentPlaygroundLon , lat, lon);
                    //Toast.makeText(getApplicationContext(), "inRange = " + inRange, Toast.LENGTH_SHORT).show();
                    if(inRange){
                        //if user is already in the playground we won't add him
                        if(!userIsInsideThePlayground)
                            mangeUserInRange(playgroundSnapshot.getKey(), playgroundName, mUserId);
                    } else {
                        //if user left the garden we will remove him
                        if(userIsInsideThePlayground)
                            mangeUserNotInRange(playgroundSnapshot ,mUserId , playgroundSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void mangeUserInRange(String playgroundID, String playgroundName, String mUserId) {

        playgrounds.child(playgroundID).child("visitors").push().child("userProfile").setValue(myUserProfile);
        //send notification to all the followers
        notifyAllFollowers(playgroundName);
    }

    private void mangeUserNotInRange(DataSnapshot playgroundSnapshot, String userId , String playgroundID){
        for (DataSnapshot userToCheck : playgroundSnapshot.child("visitors").getChildren()) {
            if (userToCheck.child("userProfile").child("uID").getValue().equals(userId))
                playgrounds.child(playgroundID).child("visitors").child(userToCheck.getKey()).setValue(null);
        }

    }

    private void notifyAllFollowers(String playgroundName) {
        myUserProfile.checkLists();
        for (String follower_email : myUserProfile.getFollowers()) {
            sendNotification(follower_email, playgroundName);
        }

    }

    private void sendNotification(final String follower_email, final String playgroundName) {
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
                                + "\"contents\": {\"en\": \"Your friend" + myUserProfile.getuName() + "just entered " + playgroundName + "\"}"
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

}

