package com.mac.jacwang.aurora20150406;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class store_info extends ActionBarActivity implements LocationListener,View.OnClickListener  {

    private static final String TAG = Static_var.TAG;

    private boolean is_GPS_on;
    MenuItem GPSitem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_info);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);

        TextView tv = (TextView)findViewById(R.id.action_bar_title);
        tv.setText("店家資訊");

        ImageView ad_img = (ImageView)findViewById(R.id.ad);
        ad_img.setOnClickListener(this);

        Intent intent = this.getIntent();
        Poi store_info = (Poi)intent.getSerializableExtra("store");

        TextView address_txt = (TextView)findViewById(R.id.address);
        address_txt.setText(store_info.getAddress());


        TextView subname_txt = (TextView)findViewById(R.id.sub_name);
        subname_txt.setText(store_info.getSubName());

        ImageView logo_img = (ImageView)findViewById(R.id.logo);
        String imageFileURL = Static_var.BRANDLOGO_PATH+store_info.getLogo();
        if( store_info.getLogo()!="null" && !store_info.getLogo().isEmpty() ) {
            //Log.i(TAG,"logo:"+pois.get(i).getLogo());
            ImageDownloader task = new ImageDownloader(imageFileURL, store_info.getLogo(), getApplicationContext(),
                    logo_img, new ImageDownloader.ImageLoaderListener() {
                @Override
                public void onImageDownloaded(ImageView img, Bitmap bmp) {
                }
            });
            task.execute();
        }

        CallImage call_img = (CallImage)findViewById(R.id.call);
        call_img.setCall(store_info.getPhone());
        call_img.setOnClickListener(this);

        CallImage menu_img = (CallImage)findViewById(R.id.menu);
        menu_img.setMenu(store_info.getMenu());
        menu_img.setOnClickListener(this);

        ImageView news_img = (ImageView)findViewById(R.id.news);
        news_img.setOnClickListener(this);

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits( 2 );
        TextView distance_val = (TextView)findViewById(R.id.distance_val);
        distance_val.setText(
                store_info.getDistance()<1000?
                    store_info.getDistance()+"":
                    nf.format(store_info.getDistance()/1000)+"" );

        TextView distance_unit = (TextView)findViewById(R.id.distance_unit);
        distance_unit.setText(store_info.getDistance()>1000?"km":"m");
    }

    @Override
    public void onResume(){
        if(GPSitem!=null)
            setGPSIcon();
        super.onResume();

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if(is_GPS_on) {
            //lms.removeUpdates(this);	//離開頁面時停止更新
        }

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
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
            break;
            case android.R.id.home:
                finish();
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
        Intent intent;
        Uri uri;
        switch(v.getId()){
            case R.id.ad:
                uri = Uri.parse("http://www.chuan-neng.com/");
                intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            break;
            case R.id.menu:
                String url = ((CallImage)v).getMenu();
                if(url.isEmpty()){
                    Toast.makeText(getApplicationContext(),"本店尚未有Menu，敬請見諒",Toast.LENGTH_SHORT).show();
                }else{
                    intent = new Intent();
                    intent.setClass(store_info.this,web_img.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }

                break;
            case R.id.call:
                String phone = ((CallImage)v).getCall();
                phone = phone.replace('#',',');
                uri = Uri.parse("tel:"+phone);
                intent= new Intent("android.intent.action.CALL",uri);
                startActivity(intent);
                break;
            case R.id.news:
                Toast.makeText(getApplicationContext(),"本店尚未有任何消息，敬請見諒",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }


    Resources r;
    public int getPx(int dp){
        r = getResources();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
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
