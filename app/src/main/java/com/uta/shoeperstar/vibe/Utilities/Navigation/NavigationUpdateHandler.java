package com.uta.shoeperstar.vibe.Utilities.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Jonny on 5/9/15.
 */
public abstract class NavigationUpdateHandler extends Handler {

    public static final int
            ROUTE_RECEIVED = 1,
            NEARING_END_OF_TURN = 2,
            MISSED_TURN = 3,
            END_NAVIGATION = 4,
            NEXT_POINT = 5,
            NEXT_TURN = 6,
            MESSAGE = 7;

    public NavigationUpdateHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ROUTE_RECEIVED:
                onRouteReceived((NavigationRoute) msg.obj);
                break;
            case NEXT_TURN:
                onNextTurn((NavigationStep) msg.obj);
                break;
            case NEARING_END_OF_TURN:
                onEndOfTurn((NavigationStep) msg.obj);
                break;
            case MISSED_TURN:
                onRouteRecalculate();
                break;
            case END_NAVIGATION:
                onEndNavigation();
                break;
            case NEXT_POINT:
                onNextPoint((List<LatLng>) msg.obj);
                break;
            case MESSAGE:
            default:
                onStringReceived((String) msg.obj);
                break;
        }
    }

    /**
     * This function will run when the HTTP request to Google Directions API returns a route
     *
     * @param route received route object parsed from Google Directions API
     */
    public abstract void onRouteReceived(NavigationRoute route);

    public abstract void onNextTurn(NavigationStep turn);

    public abstract void onEndOfTurn(NavigationStep turn);

    public abstract void onRouteRecalculate();

    public abstract void onNextPoint(List<LatLng> toPoint);

    public abstract void onEndNavigation();

    public abstract void onStringReceived(String message);

}
