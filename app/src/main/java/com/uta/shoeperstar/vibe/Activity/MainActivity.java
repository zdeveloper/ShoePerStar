package com.uta.shoeperstar.vibe.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.material.widget.TabIndicator;
import com.uta.shoeperstar.vibe.Adapter.TabsPagerAdapter;
import com.uta.shoeperstar.vibe.R;
import com.uta.shoeperstar.vibe.Utilities.VibeShoeHandler;
import com.uta.shoeperstar.vibe.Utilities.VibeShoes;


public class MainActivity extends Activity {

    private TabIndicator tabs;
    private ViewPager pager;
    private TabsPagerAdapter adapter;


    private VibeShoes vibeShoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabs = (TabIndicator) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new TabsPagerAdapter(getFragmentManager());

        pager.setAdapter(adapter);
        tabs.setViewPager(pager);

    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        //call on Bluetooth Utilities
//        vibeShoes = VibeShoes.getInstance(this);
//        //note that data will take some time to get to the shoe
//        Messenger ms = new Messenger(new VibeHandler()); //making a messenger with a new instance of handler
//
//        vibeShoes.setRightShoeListener(ms.getBinder()); //registering handler
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * This is a call back class
     */
    class VibeHandler extends VibeShoeHandler {
        @Override
        public void onStepReceived(int steps) {

        }

        @Override
        public void onBatteryLevelReceived(int batteryLevel) {

        }

        @Override
        public void onPulseEstimatedReceived(int pulses) {

        }

        @Override
        public void onPulseActualReceived(int pulses) {

        }

        @Override
        public void onStringReceived(String message) {

        }
    }





}
