package com.finalproject.dogplay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


public class BackgroundService extends Service {


    private static final String TAG = "BackgroundService";
    private boolean isRunning;
    private Context context;

    double lat, lon;

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 5*60*1000;
    private static final float LOCATION_DISTANCE = 20;
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"service created");
        this.context = this;
        this.isRunning = false;
        initializeLocationManager();


    }

    public Location getLocation(){
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
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
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

}

