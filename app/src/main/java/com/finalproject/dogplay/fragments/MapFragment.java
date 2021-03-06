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

    private View      mView;

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

        MapView mMapView = mView.findViewById(R.id.map);
        if (mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        Bundle bundle = getArguments();

        //zooming on ficus
        CameraPosition ficus = CameraPosition.builder().target(new LatLng(32.113807,34.818321)).zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(ficus));

        showPlaygrounds(bundle,googleMap);
    }

    private void showPlaygrounds(Bundle bundle, GoogleMap googleMap){

        ArrayList<String> playgrounds = bundle.getStringArrayList("EXTRA_PLAYGROUNDS");
        if(playgrounds != null)
            for (String pgStr: playgrounds){
                // playground data is saved : address###lat###lon###numOfPgUsers
                String[] playground = pgStr.split("###");
                String address = playground[0];
                double lat = Double.parseDouble(playground[1]);
                double lon = Double.parseDouble(playground[2]);
                String numOfPgUsers = playground[3];
                googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(address + ": " + " users " + numOfPgUsers));
            }

    }

}
