package com.uta.shoeperstar.vibe.Utilities.Navigation;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jonny on 5/10/15.
 */
public class NavigationRoute {

    private String polyline;
    private ArrayList<NavigationStep> steps;
    private String duration;
    private String distance;

    public NavigationRoute(JSONObject routeJson) throws JSONException {
        this.polyline = ((JSONObject) (((JSONArray) routeJson.get("routes")).getJSONObject(0))
                .get("overview_polyline")).getString("points");

        // Get steps in the route
        steps = new ArrayList<>();
        JSONArray stepsJson = ((JSONArray) ((JSONArray) ((JSONArray) routeJson.get("routes")).getJSONObject(0)
                .get("legs")).getJSONObject(0).get("steps"));
        for (int i = 0; i < stepsJson.length(); i++) {
            steps.add(new NavigationStep((JSONObject) stepsJson.get(i)));
        }

        try {
            this.duration = ((JSONObject) ((JSONArray) ((JSONArray) routeJson.get("routes")).getJSONObject(0)
                    .get("legs")).getJSONObject(0).get("distance")).getString("text");
        } catch (Exception e) {
            this.duration = null;
        }

        try {
            this.distance = ((JSONObject) ((JSONArray) ((JSONArray) routeJson.get("routes")).getJSONObject(0)
                    .get("legs")).getJSONObject(0).get("distance")).getString("distance");
        } catch (Exception e) {
            this.distance = null;
        }

    }

    public NavigationStep getCurrentStep(LatLng currentLocation) {
        if (steps == null) {
            return null;
        }

        for (NavigationStep step : steps) {
            if (step.isOnRoute(currentLocation)) {
                return step;
            }
        }

        return null;
    }

    public NavigationStep getNextStep(NavigationStep currentStep) {
        if (steps == null) {
            return null;
        }

        int currentStepIndex = steps.indexOf(currentStep);

        if (currentStepIndex != -1 && currentStepIndex != (steps.size() - 1)) {
            return steps.get(currentStepIndex + 1);
        } else {
            return null;
        }
    }

    public String getDuration() {
        return duration;
    }

    public String getDistance() {
        return distance;
    }

    public String getPolyline() {
        return polyline;
    }
}
