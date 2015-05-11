package com.uta.shoeperstar.vibe.Utilities.Navigation;

import android.location.Location;
import android.util.Log;

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
    private MANEUVERS maneuver;
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
            String maneuverStr = step.getString("maneuver");
            switch (maneuverStr) {
                case "turn-left":
                    maneuver = MANEUVERS.TURN_LEFT;
                    break;
                case "turn-right":
                    maneuver = MANEUVERS.TURN_RIGHT;
                    break;
                case "turn-slight_left":
                    maneuver = MANEUVERS.TURN_SLIGHT_LEFT;
                    break;
                case "turn-slight_right":
                    maneuver = MANEUVERS.TURN_SLIGHT_RIGHT;
                    break;
                default:
                    Log.d("NEW MANEUVER", maneuverStr);
                    maneuver = MANEUVERS.NONE;
                    break;
            }
        } catch (Exception e) {
            maneuver = MANEUVERS.NONE;
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
     * @param currentPoint
     * @return return the point that is closer to the end of turn (which should be the next point along the path)
     */
    public LatLng getNextPoint(LatLng currentPoint) {
        LatLng point1 = start, point2 = start;
        float dist1 = 99999999, dist2 = 99999999;

        // Pick two points closest to the currentPoint
        for (LatLng pointAlongPath : path) {
            float distance[] = new float[1];
            Location.distanceBetween(currentPoint.latitude, currentPoint.longitude,
                    pointAlongPath.latitude, pointAlongPath.longitude, distance);

            if (distance[0] < dist1) {
                point2 = point1;
                dist2 = dist1;

                point1 = pointAlongPath;
                dist1 = distance[0];
            }
        }

        // return the point that is closer to the end of turn (which should be the next point along the path)
        float distBetween1andEnd[] = new float[1];
        Location.distanceBetween(point1.latitude, point1.longitude,
                end.latitude, end.longitude, distBetween1andEnd);

        float distBetween2andEnd[] = new float[1];
        Location.distanceBetween(point2.latitude, point2.longitude,
                end.latitude, end.longitude, distBetween2andEnd);

        if (distBetween1andEnd[0] < distBetween2andEnd[0]) {
            return point1;
        } else {
            return point2;
        }

    }

    public boolean isOnRoute(LatLng point) {
        return PolyUtil.isLocationOnPath(point, path, true, 10);
    }

    public enum MANEUVERS {TURN_LEFT, TURN_RIGHT, TURN_SLIGHT_LEFT, TURN_SLIGHT_RIGHT, NONE}


}
