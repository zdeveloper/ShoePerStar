package com.uta.shoeperstar.vibe.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uta.shoeperstar.vibe.R;
import com.uta.shoeperstar.vibe.Utilities.NavigationUpdate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class MapViewFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener {

    private static View view;
    private static GoogleMap googleMap;
    private Marker searchResultMarker;
    private Location lastKnownLocation;
    private EditText mapSearchBox;

    // Marker info window stuff
    private boolean cameraFocusedToMarker;


    public MapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //do stuff before the ui is loaded
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
                    mapSearchBox.setText("", TextView.BufferType.EDITABLE);
                    return true;
                }
                return false;
            }
        });
    }

    private void drawDestinationMarker(final Address address) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LatLng addressLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                cameraFocusedToMarker = true;
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(addressLatLng));
                googleMap.clear();

                searchResultMarker = googleMap.addMarker(new MarkerOptions()
                        .position(addressLatLng)
                        .title(address.getFeatureName())
                        .snippet(address.getAddressLine(1))
                        .icon(BitmapDescriptorFactory.defaultMarker()));

                searchResultMarker.showInfoWindow();
            }
        });
    }

    private void drawNavigationRoute(final JSONObject navigationRoutes) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<LatLng> points;
                PolylineOptions polyLineOptions = new PolylineOptions();
                LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                bounds.include(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));

                try {
                    JSONArray steps = ((JSONArray) ((JSONArray) ((JSONArray) navigationRoutes.get("routes")).getJSONObject(0)
                            .get("legs")).getJSONObject(0).get("steps"));

                    // traversing through routes
                    for (int i = 0; i < steps.length(); i++) {
                        String encodedPolyline = ((JSONObject) steps.get(i)).getJSONObject("polyline").getString("points");

                        points = NavigationUpdate.decodePolyline(encodedPolyline);

                        // Include points of polyline to zoom the path
                        if (points.size() > 0) {
                            bounds.include(points.get(points.size() - 1));
                        }

                        polyLineOptions.addAll(points);
                        polyLineOptions.width(10);
                        polyLineOptions.color(Color.RED);
                    }

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
                    googleMap.addPolyline(polyLineOptions);

                } catch (Exception e) {
                    Log.d("ERROR", e.toString());

                    Toast.makeText(getActivity(), "Could not navigate here", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) view = inflater.inflate(R.layout.fragment_map, container, false);

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
        // TODO Add more map UI's such as search bar, start navigation button, etc.
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;

        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                lastKnownLocation = location;

                if (!cameraFocusedToMarker) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(),
                                    location.getLongitude()), 17));
                }
            }
        });

        // Setting a custom info window adapter for the google map
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {

                // Getting view from the layout file info_window layout
                View v = getActivity().getLayoutInflater().inflate(R.layout.info_window, null);

                // Getting the position from the marker
                final LatLng latLng = marker.getPosition();

                Button navigateButton = (Button) v.findViewById(R.id.navigate_button);
                navigateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("clicked?", "clicked yo");
                        new getDirectionAsync(latLng.latitude + "," + latLng.longitude).execute();
                    }
                });

                return v;
            }
        });

        googleMap.setIndoorEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        new searchAddressAsync(latLng.latitude + "," + latLng.longitude).execute();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        LatLng latLng = marker.getPosition();

        new getDirectionAsync(latLng.latitude + "," + latLng.longitude).execute();
    }

    private class searchAddressAsync extends AsyncTask<Void, Address, Address> {
        private String toSearch;

        public searchAddressAsync(String toSearch) {
            this.toSearch = toSearch;
        }

        @Override
        protected Address doInBackground(Void... voids) {

            try {
                return NavigationUpdate.getLatLongFromAddress(toSearch);
            } catch (Exception e) {
                this.cancel(true);

                return null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            Toast.makeText(getActivity(), "Could not find address", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Address result) {
            super.onPostExecute(result);

            drawDestinationMarker(result);
        }
    }

    private class getDirectionAsync extends AsyncTask<Void, JSONObject, JSONObject> {
        private String toSearch;

        public getDirectionAsync(String toSearch) {
            this.toSearch = toSearch;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            try {
                String latlngStr = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();
                return NavigationUpdate.getDirection(latlngStr, toSearch);
            } catch (Exception e) {
                this.cancel(true);

                return null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            Toast.makeText(getActivity(), "Could not get navigation route", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            drawNavigationRoute(result);
        }
    }

}
