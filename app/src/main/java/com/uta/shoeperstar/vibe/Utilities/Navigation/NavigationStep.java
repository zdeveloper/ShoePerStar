package com.uta.shoeperstar.vibe.Utilities.Navigation;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jonny on 5/10/15.
 */
public class NavigationStep {

    public static final int DISTANCE_FROM_PATH_TOLERANCE = 10;
    private float distance;
    private float duration;
    private LatLng start, end;
    private String instruction;
    private ArrayList<LatLng> path;
    private MANEUVERS maneuver = MANEUVERS.NONE;
    public NavigationStep(JSONObject step) throws JSONException {
        this.distance = ((JSONObject) step.get("distance")).getInt("value");
        this.duration = ((JSONObject) step.get("duration")).getInt("value");

        JSONObject startLocation = ((JSONObject) step.get("start_location"));
        this.start = new LatLng(startLocation.getDouble("lat"), startLocation.getDouble("lng"));

        JSONObject endLocation = ((JSONObject) step.get("end_location"));
        this.end = new LatLng(endLocation.getDouble("lat"), endLocation.getDouble("lng"));

        this.instruction = step.getString("html_instructions");

        this.path = NavigationUpdateService.decodePolyline(((JSONObject) step.get("polyline")).getString("points"));

        // Step may not always have maneuver
        try {
            String maneuverStr = step.getString("maneuver");

            if (maneuverStr.toLowerCase().contains("left")) {
                maneuver = MANEUVERS.LEFT;
            } else if (maneuverStr.toLowerCase().contains("right")) {
                maneuver = MANEUVERS.RIGHT;
            }
        } catch (Exception e) {
            maneuver = MANEUVERS.NONE;
        }
    }

    public ArrayList<LatLng> getPath() {
        return path;
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

    public MANEUVERS getManeuver() {
        return maneuver;
    }

    public float distanceUntilTurn(LatLng point) {
        float[] results = new float[1];
        Location.distanceBetween(point.latitude, point.longitude, end.latitude, end.longitude, results);

        return results[0];
    }

    /**
     * This function return the next point in path, given a current position that is along the path
     *
     * @param currentLocation
     * @return return the point that is closer to the end of turn (which should be the next point along the path)
     */
    public LatLng getNextPoint(LatLng currentLocation) {
        int closestPointIndex = getClosestPointIndex(currentLocation);

        // Closest point is at the end of the path
        if ((closestPointIndex + 1) >= path.size()) {
            return end;
        } else {
            return path.get(closestPointIndex + 1);
        }
    }

    public LatLng getClosestPoint(LatLng currentLocation) {
        int closestPointIndex = getClosestPointIndex(currentLocation);

        return path.get(closestPointIndex);
    }

    private int getClosestPointIndex(LatLng currentLocation) {
        float distance = 99999999;
        int closestPointIndexSoFar = 0;

        for (int i = 0; i < path.size(); i++) {
            float result[] = new float[1];
            Location.distanceBetween(currentLocation.latitude, currentLocation.longitude
                    , path.get(i).latitude, path.get(i).longitude, result);

            if (result[0] < distance) {
                closestPointIndexSoFar = i;
                distance = result[0];
            }
        }

        return closestPointIndexSoFar;
    }

    public boolean isOnRoute(LatLng currentLocation) {
        return PolyUtil.isLocationOnPath(currentLocation, path, false, DISTANCE_FROM_PATH_TOLERANCE);
    }

    public enum MANEUVERS {LEFT, RIGHT, NONE}


}
