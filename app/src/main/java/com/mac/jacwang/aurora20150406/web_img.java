package com.mac.jacwang.aurora20150406;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class web_img extends ActionBarActivity {

    private boolean is_GPS_on;
    MenuItem GPSitem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_img);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);

        TextView tv = (TextView)findViewById(R.id.action_bar_title);
        tv.setText("Menu");

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        WebView wv = (WebView)findViewById(R.id.webview);
        WebSettings websettings = wv.getSettings();
        websettings.setSupportZoom(true);
        websettings.setBuiltInZoomControls(true);
        websettings.setJavaScriptEnabled(true);

        wv.setWebViewClient(new WebViewClient());

        wv.loadUrl(url);
        Log.d("jac:",url);
//        wv.getSettings().setJavaScriptEnabled(true);
//        wv.requestFocus();
//        wv.loadUrl(url);
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
}
