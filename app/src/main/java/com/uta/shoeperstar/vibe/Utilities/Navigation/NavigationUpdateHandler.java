package com.uta.shoeperstar.vibe.Utilities.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by Jonny on 5/9/15.
 */
public abstract class NavigationUpdateHandler extends Handler {

    public static final int
            ROUTE_RECEIVED = 1,
            NEARING_TURN = 2,
            MISSED_TURN = 3,
            END_NAVIGATION = 4;

    public NavigationUpdateHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ROUTE_RECEIVED:
                onRouteReceived((NavigationRoute) msg.obj);
                break;
            case NEARING_TURN:
                onNearingTurn((NavigationStep) msg.obj);
                break;
            case MISSED_TURN:
                onRouteRecalculate();
                break;
            case END_NAVIGATION:
                onEndNavigation();
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }

    /**
     * This function will run when the HTTP request to Google Directions API returns a route
     *
     * @param route received route json object from Google Directions API
     */
    public abstract void onRouteReceived(NavigationRoute route);

    public abstract void onEndNavigation();

    public abstract void onNearingTurn(NavigationStep turn);

    public abstract void onRouteRecalculate();


}
