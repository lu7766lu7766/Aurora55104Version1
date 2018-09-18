package com.mac.jacwang.aurora20150406;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class backup extends ActionBarActivity implements View.OnClickListener  {

    private static final String TAG = "jac:";
    private boolean is_GPS_on;
    MenuItem GPSitem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

    }

    @Override
    public void onResume(){
        if(GPSitem!=null)
            setGPSIcon();
        super.onResume();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.add(0,0,0,"GPS_switch");
        GPSitem = menu.getItem(0);
        GPSitem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        setGPSIcon();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case 0:
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                //setGPSIcon();
                //Log.i(TAG,is_GPS_on+" gps status?");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setGPSIcon(){
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        is_GPS_on = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        GPSitem.setIcon(is_GPS_on?R.drawable.gps_on:R.drawable.gps_off);
    }

    @Override
    public void onClick(View v) {

    }
}
