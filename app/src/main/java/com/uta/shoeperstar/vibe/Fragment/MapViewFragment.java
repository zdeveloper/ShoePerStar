package com.uta.shoeperstar.vibe.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uta.shoeperstar.vibe.R;
import com.uta.shoeperstar.vibe.Utilities.Navigation.NavigationRoute;
import com.uta.shoeperstar.vibe.Utilities.Navigation.NavigationStep;
import com.uta.shoeperstar.vibe.Utilities.Navigation.NavigationUpdateService;

import java.util.ArrayList;


public class MapViewFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener {

    public static NavigationUpdateService navigationService;
    private static View view;
    private static GoogleMap googleMap;
    private Marker searchResultMarker;
    private Location lastKnownLocation;
    private EditText mapSearchBox;
    // Marker info window stuff
    private boolean cameraFocusedToMarker;
    private ServiceConnection navServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NavigationUpdateService.NavigationUpdateBinder binder = (NavigationUpdateService.NavigationUpdateBinder) service;
            navigationService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    public MapViewFragment() {
        // Required empty public constructor
    }

    public static ArrayList<LatLng> decodePolyline(String encoded) {

        ArrayList<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void initializeMapSearchBar() {
        if (getView() == null) {
            return;
        }

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

    private void drawNavigationRoute(final NavigationRoute navigationRoute) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<LatLng> points;
                PolylineOptions polyLineOptions = new PolylineOptions();
                LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                bounds.include(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));

                try {
                    points = decodePolyline(navigationRoute.getPolyline());

                    // Include points of polyline to zoom the path
                    if (points.size() > 0) {
                        bounds.include(points.get(points.size() - 1));
                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(10);
                    polyLineOptions.color(Color.RED);

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

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // This is called after on onCreateView

        initializeMapSearchBar();
        // TODO Add more map UI's such as search bar, start navigation button, etc.
        initializeNavigationUpdateService();
    }

    private void initializeNavigationUpdateService() {
        Intent intent = new Intent(getActivity(), NavigationUpdateService.class);

        getActivity().bindService(intent, navServiceConnection, Context.BIND_AUTO_CREATE);
        getActivity().startService(intent);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (navigationService == null) ; // block until the service is found

                navigationService.registerNavigationUpdateHandler(new MapNavigationUpdateHandler(navigationService.getMainLooper()));
            }
        }).start();

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
                        searchNavigationRoute(latLng.latitude + ", " + latLng.longitude);
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

        searchNavigationRoute(latLng.latitude + "," + latLng.longitude);
    }

    private void searchNavigationRoute(String toSearch) {
        navigationService.searchNavigationRoute(lastKnownLocation, toSearch);
    }

    private class searchAddressAsync extends AsyncTask<Void, Address, Address> {
        private String toSearch;

        public searchAddressAsync(String toSearch) {
            this.toSearch = toSearch;
        }

        @Override
        protected Address doInBackground(Void... voids) {

            try {
                return NavigationUpdateService.getLatLongFromAddressHttpReq(toSearch);
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

    /**
     * Callback class to handle changes in navigation as the user move along the route
     */
    private class MapNavigationUpdateHandler extends com.uta.shoeperstar.vibe.Utilities.Navigation.NavigationUpdateHandler {

        public MapNavigationUpdateHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void onRouteReceived(NavigationRoute route) {
            drawNavigationRoute(route);
            navigationService.startNavigation();
        }

        @Override
        public void onNewTurn(NavigationStep turn) {

        }

        @Override
        public void onEndOfTurn(NavigationStep turn) {

        }

        @Override
        public void onRouteRecalculate() {

        }

        @Override
        public void onNextPoint(LatLng toPoint) {
            float distance[] = new float[2];
            Location.distanceBetween(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(),
                    toPoint.latitude, toPoint.longitude, distance);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(                    // Sets the center of the map to Mountain View
                            new LatLng(lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude()))
                    .zoom(20)                   // Sets the zoom
                    .bearing(distance[1])       // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

        @Override
        public void onEndNavigation() {

        }

        @Override
        public void onStringReceived(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

}
