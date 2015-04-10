package com.uta.shoeperstar.vibe.Utilities;

import android.location.Address;
import android.net.Uri;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class Navigation {

    public static Address getLatLongFromAddress(String inputAddress) {
        String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                inputAddress + "&sensor=false";

        String result;
        try {
            result = httpGet(uri);

            JSONObject jsonObject;
            try {
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

            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            }

        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }

        return null;
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

