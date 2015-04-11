package com.uta.shoeperstar.vibe.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uta.shoeperstar.vibe.R;
import com.uta.shoeperstar.vibe.Utilities.NavigationUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class MapViewFragment extends Fragment  implements OnMapReadyCallback {

    private LocationManager locationManager;
    private Location lastKnownLocation;
    private EditText mapSearchBox;

    private final static LatLng HOME_LOCATION = new LatLng(32.731, -97.1145);		//HOME LOCATION
    private  static View view;

    private static GoogleMap map;

    public MapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //do stuff before the ui is loaded

        initializeLocationManager();
    }

    private void initializeMapSearchBar() {
        mapSearchBox = (EditText) getView().findViewById(R.id.mapSearchBox);

        mapSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_GO ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mapSearchBox.getWindowToken(), 0);

                    new searchAddressAsync(mapSearchBox.getText().toString()).execute();
                    new getDirectionAsync(mapSearchBox.getText().toString()).execute();
                    mapSearchBox.setText("", TextView.BufferType.EDITABLE);
                    return true;
                }
                return false;
            }
        });
    }

    private class searchAddressAsync extends AsyncTask<Void, Address, Address> {
        private String toSearch;

        public searchAddressAsync(String toSearch) {
            this.toSearch = toSearch;
        }

        @Override
        protected Address doInBackground(Void... voids) {

            try {
                Address result = NavigationUtilities.getLatLongFromAddress(toSearch);

                return result;
            } catch (Exception e) {
                Log.e("", "Something went wrong: ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Address result) {
            super.onPostExecute(result);

            moveMapToAddress(result);
        }
    }

    private void moveMapToAddress(final Address address) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LatLng addressLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                map.animateCamera(CameraUpdateFactory.newLatLng(addressLatLng));
                map.clear();

                Marker searchResultMarker = map.addMarker(new MarkerOptions()
                        .position(addressLatLng)
                        .title(address.getAddressLine(0))
                        .snippet(address.getAddressLine(0)).draggable(true)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.rightshoered)));

                searchResultMarker.showInfoWindow();


            }
        });
    }

    private class getDirectionAsync extends AsyncTask<Void, JSONObject, JSONObject> {
        private String toSearch;

        public getDirectionAsync(String toSearch) {
            this.toSearch = toSearch;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            try {
                return NavigationUtilities.getDirection(HOME_LOCATION.latitude + "," + HOME_LOCATION.longitude, toSearch);
            } catch (Exception e) {
                Log.e("", "Something went wrong: ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            drawRoute(result);
        }
    }

    private void drawRoute(JSONObject routes) {
        ArrayList<LatLng> points = null;
        PolylineOptions polyLineOptions = new PolylineOptions();

        try {
            JSONArray steps = ((JSONArray) ((JSONArray) ((JSONArray) routes.get("routes")).getJSONObject(0)
                    .get("legs")).getJSONObject(0).get("steps")) ;

            // traversing through routes
            for (int i = 0; i < steps.length(); i++) {
                points = new ArrayList<LatLng>();

                String encodedPolyline = ((JSONObject) steps.get(i)).getJSONObject("polyline").getString("points");

                points = NavigationUtilities.decodePoly(encodedPolyline);

                polyLineOptions.addAll(points);
                polyLineOptions.width(5);
                polyLineOptions.color(Color.RED);
            }

            map.addPolyline(polyLineOptions);

        } catch (Exception e) {
            Log.d("ERROR", e.toString());
        }
    }


    private void initializeLocationManager() {
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
        if(view == null) view = inflater.inflate(R.layout.fragment_map, container, false);

        //link UI here

        FragmentManager fm;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fm = getFragmentManager();
        } else {
            fm = getChildFragmentManager();
        }

        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // This is called after on onCreateView

        initializeMapSearchBar();
    }


    /*
    This is called whenever the map is ready
     */
    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(HOME_LOCATION, 15));
        // Other supported types include: MAP_TYPE_NORMAL,
        // MAP_TYPE_TERRAIN, MAP_TYPE_SATELLITE and MAP_TYPE_NONE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setMapToolbarEnabled(true);
    }

}
