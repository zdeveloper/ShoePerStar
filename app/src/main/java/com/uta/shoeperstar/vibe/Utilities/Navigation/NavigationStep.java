package com.uta.shoeperstar.vibe.Utilities.Navigation;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.uta.shoeperstar.vibe.Fragment.MapViewFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jonny on 5/10/15.
 */
public class NavigationStep {

    private float distance;
    private float duration;
    private LatLng start, end;
    private String instruction;
    private ArrayList<LatLng> path;
    private String maneuver;

    public NavigationStep(JSONObject step) throws JSONException {
        this.distance = ((JSONObject) step.get("distance")).getInt("value");
        this.duration = ((JSONObject) step.get("duration")).getInt("value");

        JSONObject startLocation = ((JSONObject) step.get("start_location"));
        this.start = new LatLng(startLocation.getDouble("lat"), startLocation.getDouble("lng"));

        JSONObject endLocation = ((JSONObject) step.get("end_location"));
        this.end = new LatLng(endLocation.getDouble("lat"), endLocation.getDouble("lng"));

        this.instruction = step.getString("html_instructions");

        this.path = MapViewFragment.decodePolyline(((JSONObject) step.get("polyline")).getString("points"));

        // Step may not always have maneuver
        try {
            this.maneuver = step.getString("maneuver");
        } catch (Exception e) {
            maneuver = null;
        }
    }

    public float getDistance() {
        return distance;
    }

    public float getDuration() {
        return duration;
    }

    public LatLng getStartLocation() {
        return start;
    }

    public LatLng getEndLocation() {
        return end;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getManeuver() {
        return maneuver;
    }

    public float distanceToTurn(LatLng point) {
        float[] results = new float[1];
        Location.distanceBetween(point.latitude, point.longitude, end.latitude, end.longitude, results);

        return results[0];
    }

    public float[] getNextPoint(LatLng point) {
        float result[] = new float[3];

        return result;
    }

    public boolean isOnRoute(LatLng point) {
        return PolyUtil.isLocationOnPath(point, path, true);
    }

}
