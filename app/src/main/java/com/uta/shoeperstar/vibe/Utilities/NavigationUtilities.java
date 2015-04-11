package com.uta.shoeperstar.vibe.Utilities;

import android.location.Address;
import android.net.Uri;

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
import java.util.ArrayList;
import java.util.Locale;

public class NavigationUtilities {

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

    public static JSONObject getDirection(String origin, String destination) throws Exception {
        String uri = "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + origin
                + "&destination=" + destination
                + "&mode=walking";

        String result = httpGet(uri);

        return new JSONObject(result);
    }

    public static Address getLatLongFromAddress(String inputAddress) throws Exception {
        String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                inputAddress + "&sensor=false";

        String result = httpGet(uri);

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

    public static String httpGet(String uri) throws IOException{
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
}

