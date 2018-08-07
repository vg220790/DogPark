package com.finalproject.dogplay.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finalproject.dogplay.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView   mMapView;
    View      mView;

    ArrayList<String> playgrounds;

    public MapFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view,bundle);

        mMapView = mView.findViewById(R.id.map);
        if (mMapView!= null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        Bundle bundle = getArguments();

        //zooming on ficus
        CameraPosition ficus = CameraPosition.builder().target(new LatLng(32.113807,34.818321)).zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(ficus));

        showPlaygrounds(bundle,googleMap);
    }

    public void showPlaygrounds(Bundle bundle, GoogleMap googleMap){

        playgrounds = bundle.getStringArrayList("EXTRA_PLAYGROUNDS");
        if(playgrounds != null)
            for (String pgStr: playgrounds){
                int numOfPgUsers =0;
                String[] pgUsers;
                String[] playground = pgStr.split("-");
                String address = playground[0];
                double lat = Double.parseDouble(playground[1]);
                double lon = Double.parseDouble(playground[2]);
                if (playground.length > 3){ //if there are users in the park
                    String users = playground[3];
                    if (users.contains("[") && users.contains("]")){
                        users = users.substring(1,users.length()-2); // getting rid of "[" and "]"
                    }
                    if (!users.equals("")){
                        pgUsers = users.split(",");
                        numOfPgUsers = pgUsers.length;
                    }
                }
                googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(address + ": " + " users " + numOfPgUsers));
            }

    }

}
