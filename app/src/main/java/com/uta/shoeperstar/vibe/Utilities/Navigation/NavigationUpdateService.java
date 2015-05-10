package com.uta.shoeperstar.vibe.Utilities.Navigation;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class NavigationUpdateService extends Service implements LocationListener {

    private final IBinder mBinder = new NavigationUpdateBinder();
    private Looper mServiceLooper;

    private Messenger mapViewMessenger;

    private NavigationRoute route;
    private NavigationStep currentStep;

    public NavigationUpdateService() {
    }

    public static JSONObject getDirectionHttpReq(String origin, String destination) throws Exception {
        String uri = "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + origin
                + "&destination=" + destination
                + "&mode=walking";

        String result = httpRequest(uri);

        return new JSONObject(result);
    }

    public static Address getLatLongFromAddressHttpReq(String inputAddress) throws Exception {
        String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                inputAddress + "&sensor=false";

        String result = httpRequest(uri);

        JSONObject jsonObject;

        jsonObject = new JSONObject(result);

        double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lng");

        double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lat");

        String addressStr = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                .getString("formatted_address");

        Address resultAddress = new Address(Locale.US);
        resultAddress.setLongitude(lng);
        resultAddress.setLatitude(lat);
        resultAddress.setAddressLine(0, addressStr);

        return resultAddress;
    }

    public static String httpRequest(String uri) throws IOException {
        // Format URI
        uri = Uri.parse(uri.replace(' ', '+')).toString();
        Uri.encode(uri, "UTF-8");

        // Make HTTP get
        HttpGet httpGet = new HttpGet(uri);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        // get response
        response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();
        int b;
        while ((b = stream.read()) != -1) {
            stringBuilder.append((char) b);
        }

        return stringBuilder.toString();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        currentStep = route.getCurrentStep(latLng);
        Log.d("Navigating", "Distance until turn: " + currentStep.distanceToTurn(latLng));

        // TODO Display next step
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Register a Maps view, to update navigation data such as current route, ETA's, speed, etc.
     * while navigating
     *
     * @param handler user implemented handler {@link NavigationUpdateHandler}
     */
    public void registerNavigationUpdateHandler(final Handler handler) {
        final Messenger msg = new Messenger(handler);
        mapViewMessenger = new Messenger(msg.getBinder());

    }

    public NavigationRoute getRoute() {
        return route;
    }

    public NavigationStep getCurrentStep() {
        return currentStep;
    }

    public void searchNavigationRoute(Location lastKnownLocation, String toSearch) {
        new getDirectionAsync(lastKnownLocation, toSearch).execute();
    }

    private void sendMessage(int what, Object message) throws RemoteException {
        if (mapViewMessenger == null) {
            throw new RemoteException("Messenger is null");
        }

        Message msg = Message.obtain(null, what, message);
        mapViewMessenger.send(msg);
    }

    public class NavigationUpdateBinder extends Binder {
        public NavigationUpdateService getService() {
            return NavigationUpdateService.this;
        }
    }

    private class getDirectionAsync extends AsyncTask<Void, JSONObject, JSONObject> {
        private String toSearch;
        private Location lastKnownLocation;

        public getDirectionAsync(Location lastKnownLocation, String toSearch) {
            this.lastKnownLocation = lastKnownLocation;
            this.toSearch = toSearch;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            try {
                String latlngStr = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();
                return NavigationUpdateService.getDirectionHttpReq(latlngStr, toSearch);
            } catch (Exception e) {
                Log.d("err", "nav http req err", e);
                this.cancel(true);

                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            try {
                route = new NavigationRoute(result);

                sendMessage(NavigationUpdateHandler.ROUTE_RECEIVED, route);

            } catch (Exception e) {
                Log.d("err", "Problem with getting route json object", e);
            }
        }
    }

}

