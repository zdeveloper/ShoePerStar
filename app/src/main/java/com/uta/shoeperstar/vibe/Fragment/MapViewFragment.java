package com.uta.shoeperstar.vibe.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
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


public class MapViewFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {

    private static View view;
    private static GoogleMap googleMap;
    private Location lastKnownLocation;
    private EditText mapSearchBox;

    // Marker info window stuff
    private boolean cameraFocusedToMarker;
    private View markerInfoWindow;
    private TextView markerText;
    private TextView markerButton;
    private LatLng trackedPosition;
    private int popupXOffset;
    private int popupYOffset;
    private Handler markerInfoWindowHandler;
    private Runnable positionUpdaterRunnable;
    private ViewTreeObserver.OnGlobalLayoutListener infoWindowLayoutListener;
    private AbsoluteLayout.LayoutParams overlayLayoutParams;

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
                //TODO googleMap.clear();

                Marker searchResultMarker = googleMap.addMarker(new MarkerOptions()
                        .position(addressLatLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_destination_marker))
                        .title("Cum get me bae!"));
            }
        });
    }

    private void drawNavigationRoute(final JSONObject navigationRoutes) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<LatLng> points;
                PolylineOptions polyLineOptions = new PolylineOptions();

                try {
                    JSONArray steps = ((JSONArray) ((JSONArray) ((JSONArray) navigationRoutes.get("routes")).getJSONObject(0)
                            .get("legs")).getJSONObject(0).get("steps"));

                    // traversing through routes
                    for (int i = 0; i < steps.length(); i++) {
                        String encodedPolyline = ((JSONObject) steps.get(i)).getJSONObject("polyline").getString("points");

                        points = NavigationUtilities.decodePolyline(encodedPolyline);

                        polyLineOptions.addAll(points);
                        polyLineOptions.width(10);
                        polyLineOptions.color(Color.GREEN);
                    }

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
        initializeMarkerInfoWindow();
        // TODO Add more map UI's such as search bar, start navigation button, etc.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        markerInfoWindow.getViewTreeObserver().removeGlobalOnLayoutListener(infoWindowLayoutListener);
        markerInfoWindowHandler.removeCallbacks(positionUpdaterRunnable);
        markerInfoWindowHandler = null;
    }

    public void initializeMarkerInfoWindow() {
        markerInfoWindow = getView().findViewById(R.id.container_popup);
        infoWindowLayoutListener = new InfoWindowLayoutListener();
        markerInfoWindow.getViewTreeObserver().addOnGlobalLayoutListener(infoWindowLayoutListener);
        overlayLayoutParams = (AbsoluteLayout.LayoutParams) markerInfoWindow.getLayoutParams();

        markerText = (TextView) markerInfoWindow.findViewById(R.id.map_marker_text);
        markerButton = (TextView) markerInfoWindow.findViewById(R.id.map_marker_button);

        markerInfoWindowHandler = new Handler(Looper.getMainLooper());
        positionUpdaterRunnable = new PositionUpdaterRunnable();
        markerInfoWindowHandler.post(positionUpdaterRunnable);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Projection projection = googleMap.getProjection();
        trackedPosition = marker.getPosition();
        Point trackedPoint = projection.toScreenLocation(trackedPosition);
        trackedPoint.y -= popupYOffset / 2;
        LatLng newCameraLocation = projection.fromScreenLocation(trackedPoint);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(newCameraLocation));

        markerText.setText(marker.getTitle());
        markerButton.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerInfoWindow.setVisibility(View.INVISIBLE);
                new getDirectionAsync(marker.getPosition().latitude + "," + marker.getPosition().longitude).execute();
            }
        });

        markerInfoWindow.setVisibility(View.VISIBLE);

        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        markerInfoWindow.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        this.googleMap = map;

        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                lastKnownLocation = location;

                Marker currentLocationMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_current_location_marker))
                        .title("were my luv at??"));

                if (!cameraFocusedToMarker) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(),
                                    location.getLongitude()), 15));
                }
            }
        });

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        new searchAddressAsync(latLng.latitude + "," + latLng.longitude).execute();
    }

    private class InfoWindowLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            popupXOffset = markerInfoWindow.getWidth() / 2;
            popupYOffset = markerInfoWindow.getHeight();
        }
    }

    private class PositionUpdaterRunnable implements Runnable {
        private int lastXPosition = Integer.MIN_VALUE;
        private int lastYPosition = Integer.MIN_VALUE;
        private int markerHeight = getResources().getDrawable(R.drawable.map_marker_pin).getIntrinsicHeight();

        @Override
        public void run() {
            markerInfoWindowHandler.postDelayed(this, 16);

            if (trackedPosition != null && markerInfoWindow.getVisibility() == View.VISIBLE) {
                Point targetPosition = googleMap.getProjection().toScreenLocation(trackedPosition);

                if (lastXPosition != targetPosition.x || lastYPosition != targetPosition.y) {
                    overlayLayoutParams.x = targetPosition.x - popupXOffset;
                    overlayLayoutParams.y = targetPosition.y - popupYOffset - markerHeight - 30;
                    markerInfoWindow.setLayoutParams(overlayLayoutParams);

                    lastXPosition = targetPosition.x;
                    lastYPosition = targetPosition.y;
                }
            }
        }
    }

    private class searchAddressAsync extends AsyncTask<Void, Address, Address> {
        private String toSearch;

        public searchAddressAsync(String toSearch) {
            this.toSearch = toSearch;
        }

        @Override
        protected Address doInBackground(Void... voids) {

            try {
                return NavigationUtilities.getLatLongFromAddress(toSearch);
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
                return NavigationUtilities.getDirection(latlngStr, toSearch);
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
