package com.mac.jacwang.aurora20150406;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;



import static android.location.LocationManager.GPS_PROVIDER;


public class MainActivity extends ActionBarActivity implements View.OnClickListener,LocationListener {

    private static final String TAG = Static_var.TAG;

    private boolean is_GPS_on,getService=false;
    private Context context;
    MenuItem GPSitem = null;
    ImageView send_out_find_store, send_out_record, my_favorite, member_special;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        //getSupportActionBar().setDisplayOptions(DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        send_out_find_store = (ImageView) findViewById(R.id.send_out_find_store);
        send_out_record = (ImageView) findViewById(R.id.send_out_record);
        my_favorite = (ImageView) findViewById(R.id.my_favorite);
        member_special = (ImageView) findViewById(R.id.member_special);

        send_out_find_store.setOnClickListener(this);
        send_out_record.setOnClickListener(this);
        my_favorite.setOnClickListener(this);
        member_special.setOnClickListener(this);

        ////GPS GETTING
        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            getService = true;	//確認開啟定位服務
            locationServiceInitial();
        }
    }

    @Override
    public void onResume() {
        if (GPSitem != null)
            setGPSIcon();
        if(getService) {
            lms.requestLocationUpdates(bestProvider,0, 0, this);
            //服務提供者、更新頻率60000毫秒=1分鐘、最短距離、地點改變時呼叫物件
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(getService) {
            lms.removeUpdates(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.add(0, 0, 0, "GPS_switch");
        GPSitem = menu.getItem(0);
        GPSitem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        setGPSIcon();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setGPSIcon() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        is_GPS_on = service.isProviderEnabled(GPS_PROVIDER);
        GPSitem.setIcon(is_GPS_on ? R.drawable.gps_on : R.drawable.gps_off);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_out_find_store:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, send_out_find_store.class);
                startActivity(intent);
                break;
            case R.id.send_out_record:
                Toast.makeText(getApplicationContext(),"本功能尚未完成，敬請期待！",Toast.LENGTH_SHORT).show();
                break;
            case R.id.my_favorite:
                Toast.makeText(getApplicationContext(),"本功能尚未完成，敬請期待！",Toast.LENGTH_SHORT).show();
                break;
            case R.id.member_special:
                Toast.makeText(getApplicationContext(),"本功能尚未完成，敬請期待！",Toast.LENGTH_SHORT).show();
                break;
        }
        //connect to server
    }

    private LocationManager lms;
    private String bestProvider = LocationManager.GPS_PROVIDER;	//最佳資訊提供者
    private void locationServiceInitial() {
        lms = (LocationManager) getSystemService(LOCATION_SERVICE);	//取得系統定位服務
        Criteria criteria = new Criteria();	//資訊提供者選取標準
        bestProvider = lms.getBestProvider(criteria, true);
        Static_var.location = lms.getLastKnownLocation(bestProvider);	//使用GPS定位座標
    }

    @Override
    public void onLocationChanged(Location location) {
        Static_var.location = location;
        Log.d(TAG,location.getLatitude()+","+location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}