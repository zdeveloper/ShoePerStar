package com.uta.shoeperstar.vibe.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.uta.shoeperstar.vibe.R;


public class MapViewFragment extends Fragment  implements OnMapReadyCallback {

    LocationManager locationManager;
    Location lastKnownLocation;

    private final static LatLng HOME_LOCATION = new LatLng(32.731, -97.1145);		//HOME LOCATION

    public MapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //do stuff before the ui is loaded

        locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        lastKnownLocation =
                locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);


        //link UI here
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // This is called after on onCreateView

        //do stuff here after ui is initialized
    }


    /*
    This is called whenever the map is ready
     */
    @Override
    public void onMapReady(GoogleMap map) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(HOME_LOCATION, 15));
        // Other supported types include: MAP_TYPE_NORMAL,
        // MAP_TYPE_TERRAIN, MAP_TYPE_SATELLITE and MAP_TYPE_NONE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?"+"saddr="
                        + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude() + "&daddr="
                        + (lastKnownLocation.getLatitude()+0.2) + "," + (lastKnownLocation.getLongitude()-.3)));
        startActivity(intent);

    }

}
