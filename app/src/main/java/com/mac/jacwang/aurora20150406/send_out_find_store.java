package com.mac.jacwang.aurora20150406;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class send_out_find_store extends ActionBarActivity implements LocationListener,View.OnClickListener,View.OnTouchListener  {

    private static final String TAG = Static_var.TAG;

    private boolean is_GPS_on,getService=false;
    MenuItem GPSitem = null;

    ArrayList<Poi> pois = new ArrayList<>();
    LinearLayout store_list_container;
    int page=1;
    boolean loading = false;

    private double mlatitude,mlongitude;

    LazyScrollView store_list_scorll;
    LinearLayout left_menu;
    TextView address_txt;
    String address_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_out_find_store);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        //暴力網路連線，未來要修改
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);

        TextView tv = (TextView)findViewById(R.id.action_bar_title);
        tv.setText("外送專區");

        store_list_container = (LinearLayout)findViewById(R.id.store_list_container);
        left_menu = (LinearLayout)findViewById(R.id.left_menu);
        new DBConnector(mHandler,"type,null,null,null,null,null,null,null",5);

//        store_list_scorll=(LazyScrollView)findViewById(R.id.store_list_scorll);
//        store_list_scorll.getView();
//        store_list_scorll.setOnScrollListener(new LazyScrollView.OnScrollListener() {
//
//            @Override
//            public void onBottom() {
//                // TODO Auto-generated method stub
//                Log.d(TAG,"------滾動到最下方------");
//                if(page>1) {
//                    new DBConnector(mHandler,
//                            "store,"+
//                                    Static_var.search_type  +","+
//                                    Static_var.search_brand +","+
//                                    Static_var.search_city  +","+
//                                    Static_var.search_dist  +","+
//                                    mlatitude   +","+
//                                    mlongitude  +","+
//                                    (page++)
//                            ,2);
//                }
//            }
//
//        });
        /*--------------------------------------GPS設定--------------------------------------*/
        //取得系統定位服務
        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            getService = true;	//確認開啟定位服務
            locationServiceInitial();
        }else{
            Toast.makeText(this, "請開啟定位或網路服務", Toast.LENGTH_LONG).show();
        }

        //getNewList();
        initValues();
        content.setOnTouchListener(this);
    }

    private LocationManager lms;
    private String bestProvider = LocationManager.GPS_PROVIDER;	//最佳資訊提供者
    private void locationServiceInitial() {
        lms = (LocationManager) getSystemService(LOCATION_SERVICE);	//取得系統定位服務
        Criteria criteria = new Criteria();	//資訊提供者選取標準
        bestProvider = lms.getBestProvider(criteria, true);
        Location location = lms.getLastKnownLocation(bestProvider);	//使用GPS定位座標

        short i=0;
        while(location  == null){
            if(i%2==0)
                location = lms.getLastKnownLocation(bestProvider);
            else
                location = lms.getLastKnownLocation("network");
            if(i++==1000)
                break;
        }
        Log.d(TAG,"nowc.location:"+location.getLatitude()+","+location.getLongitude());
        if(location==null&& Static_var.location!=null){
            location= Static_var.location;
        }
        if(location!=null){
            mlatitude=location.getLatitude();
            mlongitude=location.getLongitude();
            address_val = getAddressByLocation(location).substring(3);
            address_txt = (TextView)findViewById(R.id.address);
            address_txt.setText("您的位置："+address_val);
            getNewList();
        }
    }


    @Override
    public void onResume(){
        if(GPSitem!=null)
            setGPSIcon();
        if(getService) {
            lms.requestLocationUpdates(bestProvider,1000, 0, this);
            //服務提供者、更新頻率60000毫秒=1分鐘、最短距離、地點改變時呼叫物件
        }
        super.onResume();
        //////////////////////////////////////////////////縣市選擇視窗
        startService(new Intent(this,PosBrand_selector.class));
        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("my-event"));
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            Static_var.search_city = intent.getStringExtra("city");
            Static_var.search_dist = intent.getStringExtra("dist");
            Static_var.search_brand = intent.getStringExtra("brand");
            getNewList();
            //Log.d(TAG, "Got message: " + area+"^^"+brand);
        }
    };
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
        if(is_GPS_on) {
            lms.removeUpdates(this);	//離開頁面時停止更新
        }
        //////////////////////////////////////////////////縣市選擇視窗
        stopService(new Intent(this,PosBrand_selector.class));
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
        switch(v.getId())
        {
            case R.id.call:
                CallImage call = (CallImage)v.findViewById(R.id.call);
                int i = call.getI();
                Intent it = new Intent();
                it.setClass(send_out_find_store.this,store_info.class);
                it.putExtra("store",pois.get(i));
                startActivity(it);
            break;
        }
    }

    private void getNewList()
    {

        page = 1;
        pois.clear();
        store_list_container.removeAllViews();
        addLoad2Container(store_list_container);

        new DBConnector(mHandler,
                            "store,"+
                            Static_var.search_type  +","+
                            Static_var.search_brand +","+
                            Static_var.search_city  +","+
                            Static_var.search_dist  +","+
                            mlatitude   +","+
                            mlongitude  +","+
                            (page++)
                        ,2);
        Log.d(TAG,"store,"+Static_var.search_type  +","+Static_var.search_brand +","+
                Static_var.search_city  +","+Static_var.search_dist  +","+mlatitude   +","+
                mlongitude  +","+ page);
    }

    private void addLoad2Container(LinearLayout container){
        //container.removeAllViews();
        LoadingGif loadingGif = new LoadingGif(this);
        int gif_height = loadingGif.getMovieHeight();
        ((LazyScrollView)container.getParent()).set_under_height(gif_height);
        loadingGif.setId(R.id.loading);
        container.setGravity(Gravity.CENTER);
        container.addView(loadingGif);
    }

    private void setNewList()
    {
        store_list_container.removeAllViews();

        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int len = pois.size();

        for(int i=0;i<len;i++)
        {
            View v = li.inflate(R.layout.store_list, null);

            ImageView store_logo = (ImageView)v.findViewById(R.id.store_logo);
            String imageFileURL = Static_var.BRANDLOGO_PATH+pois.get(i).getLogo();
            if( pois.get(i).getLogo()!="null" && !pois.get(i).getLogo().isEmpty() ) {
                ImageDownloader task = new ImageDownloader(imageFileURL, pois.get(i).getLogo(), getApplicationContext(),
                        store_logo, new ImageDownloader.ImageLoaderListener() {
                    @Override
                    public void onImageDownloaded(ImageView img, Bitmap bmp) {
                    }
                });
                task.execute();
            }

            final TextView store_address = (TextView)v.findViewById(R.id.store_address);
            store_address.setText(pois.get(i).getAddress());
            /*store_name.post(new Runnable() {
                @Override
                public void run() {
                    float textSize = store_name.getTextSize();
                    while(store_name.getWidth()<store_name.getText().toString().length()*textSize--);
                    store_name.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
                }
            });*/
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits( 2 );
            TextView distance_val = (TextView)v.findViewById(R.id.distance_val);
            distance_val.setText(
                    pois.get(i).getDistance()<1000?
                            String.valueOf(pois.get(i).getDistance()):
                            String.valueOf(nf.format(pois.get(i).getDistance()/1000))
            );
            TextView distance_unit = (TextView)v.findViewById(R.id.distance_unit);
            distance_unit.setText(pois.get(i).getDistance()<1000?"m":"km");
            CallImage call = (CallImage)v.findViewById(R.id.call);
            call.setCall(pois.get(i).getPhone());
            call.getCall();
            call.setIndex(pois.get(i).getId());
            call.setI(i);
            call.setOnClickListener(this);

            store_list_container.addView(v);
        }
        addLoad2Container(store_list_container);
    }

    //帶入使用者及景點店家經緯度可計算出距離  SERVER做掉了
    public double countDistance(double latitude1,double longitude1,double latitude2,double longitude2)
    {
        double radLatitude1 = latitude1 * Math.PI / 180;
        double radLatitude2 = latitude2 * Math.PI / 180;
        double l = radLatitude1 - radLatitude2;
        double p = longitude1 * Math.PI / 180 - longitude2 * Math.PI / 180;
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(l / 2), 2)
                + Math.cos(radLatitude1) * Math.cos(radLatitude2)
                * Math.pow(Math.sin(p / 2), 2)));
        distance = distance * 6378137.0;
        distance = Math.round(distance * 10000) / 10000;

        return distance ;
    }

    //List排序，依照距離由近開始排列，第一筆為最近，最後一筆為最遠  SERVER做掉了
    private void distanceSort(ArrayList<Poi> poi)
    {
        Collections.sort(poi, new Comparator<Poi>() {
            @Override
            public int compare(Poi poi1, Poi poi2) {
                return poi1.getDistance() < poi2.getDistance() ? -1 : 1;
            }
        });
    }



    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            String result = null;
            switch (msg.what)
            {
                // 顯示網路上抓取的資料
                case Static_var.STORE_DATA:
                    if (msg.obj instanceof String)
                        result = (String) msg.obj;
                    if (result.trim().equals("[]")&&page>=2)
                    {
                        store_list_container.removeView(store_list_container.findViewById(R.id.loading));
                        //store_list_container.removeAllViews();
                        if(page==2) {
                            TextView tv = new TextView(getApplicationContext());
                            tv.setText("目前尚無資料");
                            tv.setTextSize(30);
                            tv.setGravity(Gravity.CENTER);
                            tv.setTextColor(Color.parseColor("#444444"));
                            store_list_container.addView(tv);
                        }
                        //Toast.makeText(getApplicationContext(), "請確認網路連線及GPS開啟", Toast.LENGTH_LONG).show();
                        break;
                    }
                    else if (result != null)
                    {
                        store_list_container.removeView(store_list_container.findViewById(R.id.loading));
                        //Log.i(TAG,"result:"+result);
                        try {
                            JSONArray jsonArray = new JSONArray(result);

                            for(int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonData = jsonArray.getJSONObject(i);
                                Poi poi = new Poi(Integer.valueOf(jsonData.getString("fi_id")));
                                poi.setBrandName(jsonData.getString("fv_brand_name"));
                                poi.setAddress(jsonData.getString("fv_address"));
                                    //new Poi(jsonData.getString("fv_brand_name"),jsonData.getString("fv_address"));
                                poi.setLogo(jsonData.getString("fv_logo"));
                                if(!jsonData.getString("fv_menu").isEmpty())
                                    poi.setMenu(Static_var.BRANDMENU_PATH+jsonData.getString("fv_menu"));
                                poi.setPhone(jsonData.getString("fv_phone"));
                                poi.setSubName(jsonData.getString("fv_subname"));

                                //poi.setLongitude(Double.parseDouble(jsonData.getString("ff_longitude")));
                                //poi.setLatitude(Double.parseDouble(jsonData.getString("ff_latitude")));
                                NumberFormat nf = NumberFormat.getInstance();
                                nf.setMaximumFractionDigits( 0 );
                                poi.setDistance(Math.floor(Double.parseDouble(jsonData.getString("distance"))));

                                pois.add(poi);
                                //////////////////////////////////////////////////

                            }
                            //distanceSort(pois);
                            setNewList();

                        } catch(Exception e) {
                            Toast.makeText(getApplicationContext(), "請確認網路連線及GPS開啟", Toast.LENGTH_LONG).show();
                            Log.e(TAG, e.toString());
                        }
                    }
                break;
                case Static_var.TYPE_DATA:
                    if (msg.obj instanceof String)
                        result = (String) msg.obj;
                    if(result!=null||!result.trim().equals("[]")) {
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonData = jsonArray.getJSONObject(i);
                                TypeImage ti = new TypeImage(getApplicationContext());

                                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(-1,-1, 1); // , 1是可選寫的
                                parms.setMargins(0,getPx(15),0,getPx(5));

                                parms.width = getPx(100);
                                parms.height = getPx(100);
                                ti.setLayoutParams(parms);

                                ti.setIndex(Integer.valueOf(jsonData.getString("fi_id")));
                                String imageFileURL = Static_var.TYPELOGO_PATH + jsonData.getString("fv_logo");
                                ImageDownloader task = new ImageDownloader(imageFileURL, jsonData.getString("fv_logo"), getApplicationContext(),
                                        ti, new ImageDownloader.ImageLoaderListener() {
                                    @Override
                                    public void onImageDownloaded(ImageView img, Bitmap bmp) {
                                        //Log.i(TAG,"img Loaded!!");
                                    }
                                });
                                task.execute();
                                ti.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(Static_var.search_type != ((TypeImage) v).getIndex()) {
                                            stopService(new Intent(getApplicationContext(), PosBrand_selector.class));
                                            startService(new Intent(getApplicationContext(), PosBrand_selector.class));
                                            Static_var.search_brand = "null";
                                            Static_var.search_city = "null";
                                            Static_var.search_dist = "null";
                                            Static_var.search_type = ((TypeImage) v).getIndex();
                                            getNewList();
                                        }
                                    }
                                });
                                left_menu.addView(ti);
                                loading = false;
                                //Log.d(TAG,ti.getHeight()+"^"+ti.getWidth());
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "請確認網路連線及GPS開啟", Toast.LENGTH_LONG).show();
                            Log.e(TAG, e.toString());
                        }
                    }
                break;
            }
        }
    };
    // handler for received Intents for the "my-event" event

    @Override
    public void onLocationChanged(Location location) {
        mlatitude=location.getLatitude();
        mlongitude=location.getLongitude();
        address_val = getAddressByLocation(location).substring(3);
        address_txt.setText("您的位置："+address_val);
    }

    public String getAddressByLocation(Location location) {
        String returnAddress = "";
        try {
            if (location != null) {
                Double longitude = location.getLongitude();        //取得經度
                Double latitude = location.getLatitude();        //取得緯度

                Geocoder gc = new Geocoder(this, Locale.TRADITIONAL_CHINESE);        //地區:台灣
                //自經緯度取得地址
                List<Address> lstAddress = gc.getFromLocation(latitude, longitude, 1);

                if (!Geocoder.isPresent()){ //Since: API Level 9
                    returnAddress = "Sorry! Geocoder service not Present.";
                }
                returnAddress = lstAddress.get(0).getAddressLine(0);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return returnAddress;
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

    private void getNewStore(){
        if( page>1 && !loading) {
            new DBConnector(mHandler,
                    "store,"+
                            Static_var.search_type  +","+
                            Static_var.search_brand +","+
                            Static_var.search_city  +","+
                            Static_var.search_dist  +","+
                            mlatitude   +","+
                            mlongitude  +","+
                            (page++)
                    ,2);
            loading = true;
        }
    }
    ///////////////////////////////////////////////////////////////menu setting

    /**
     * 滚动显示和隐藏menu时，手指滑动需要达到的速度。
     */
    public static final int SNAP_VELOCITY = 200;

    /**
     * 屏幕宽度值。
     */
    private int screenWidth,screenHeight;

    /**
     * menu最多可以滑动到的左边缘。值由menu布局的宽度来定，marginLeft到达此值之后，不能再减少。
     */
    private int leftEdge;

    /**
     * menu最多可以滑动到的右边缘。值恒为0，即marginLeft到达0之后，不能增加。
     */
    private int rightEdge = 0;

    /**
     * menu完全显示时，留给content的宽度值。
     */
    private int menuPadding = 400;

    /**
     * 主内容的布局。
     */
    private View content;

    /**
     * menu的布局。
     */
    private View menu;

    /**
     * menu布局的参数，通过此参数来更改leftMargin的值。
     */
    private LinearLayout.LayoutParams menuParams;

    /**
     * 记录手指按下时的横坐标。
     */
    private float xDown,yDown;

    /**
     * 记录手指移动时的横坐标。
     */
    private float xMove,yMove;

    /**
     * 记录手机抬起时的横坐标。
     */
    private float xUp,yUp;

    /**
     * menu当前是显示还是隐藏。只有完全显示或隐藏menu时才会更改此值，滑动过程中此值无效。
     */
    private boolean isMenuVisible;

    /**
     * 用于计算手指滑动的速度。
     */
    private VelocityTracker mVelocityTracker;

    /**
     * 初始化一些关键性数据。包括获取屏幕的宽度，给content布局重新设置宽度，给menu布局重新设置宽度和偏移距离等。
     */
    private void initValues() {

        menuPadding = screenWidth*4/7;

        content = findViewById(R.id.store_list_scorll);
        menu = findViewById(R.id.type_menu);
        menuParams = (LinearLayout.LayoutParams) menu.getLayoutParams();
        // 将menu的宽度设置为屏幕宽度减去menuPadding
        menuParams.width = screenWidth - menuPadding;
        // 左边缘的值赋值为menu宽度的负数
        leftEdge = -menuParams.width;
        // menu的leftMargin设置为左边缘的值，这样初始化时menu就变为不可见
        menuParams.leftMargin = leftEdge;
        // 将content的宽度设置为屏幕宽度
        content.getLayoutParams().width = screenWidth;
    }

    int scroll_type = 0;//0 閒置 1 上下 2 左右
    int scrollY;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时，记录按下时的横坐标
                xDown = event.getRawX();
                yDown = event.getRawY();
                scroll_type = 0;
                scrollY = ((ScrollView)content).getScrollY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 手指移动时，对比按下时的横坐标，计算出移动的距离，来调整menu的leftMargin值，从而显示和隐藏menu
                xMove = event.getRawX();
                yMove = event.getRawY();
                int distanceX = (int) (xMove - xDown);
                int distanceY = (int) (yMove - yDown);
                //Log.d(TAG,scroll_type+"^"+Math.abs(distanceY)+"^"+Math.abs(distanceX));
                if( scroll_type==0 && (Math.abs(distanceY)>10 || Math.abs(distanceX)>10 ) ){
                    if( Math.abs(distanceY)>Math.abs(distanceX) )
                        scroll_type = 1;
                    else
                        scroll_type = 2;
                }
                switch(scroll_type){
                    case 1:
                        ((ScrollView)content).scrollTo(0,scrollY-distanceY);
                        int totalHeight = getPx(((LazyScrollView)content).getMeasuredHeight());
                        if( totalHeight <= content.getScrollY() + content.getHeight() + ((LazyScrollView)content).get_under_height()){
                            Log.d(TAG,"totalHeight:"+totalHeight+"^scrollY:"+content.getScrollY()+"^Height:"+content.getHeight()+"^under_height:"+((LazyScrollView)content).get_under_height()+"^"+loading);
                            getNewStore();
                        }
                        break;
                    case 2:
                        if (isMenuVisible) {
                            menuParams.leftMargin = distanceX;
                        } else {
                            menuParams.leftMargin = leftEdge + distanceX;
                        }
                        if (menuParams.leftMargin < leftEdge) {
                            menuParams.leftMargin = leftEdge;
                        } else if (menuParams.leftMargin > rightEdge) {
                            menuParams.leftMargin = rightEdge;
                        }
                        menu.setLayoutParams(menuParams);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:

                // 手指抬起时，进行判断当前手势的意图，从而决定是滚动到menu界面，还是滚动到content界面
                xUp = event.getRawX();
                if (wantToShowMenu() && scroll_type==2) {
                    if (shouldScrollToMenu()) {
                        scrollToMenu();
                    } else {
                        scrollToContent();
                    }
                } else if (wantToShowContent()) {
                    if (shouldScrollToContent()) {
                        scrollToContent();
                    } else {
                        scrollToMenu();
                    }
                }
                recycleVelocityTracker();
                break;
        }
        return true;
    }

    /**
     * 判断当前手势的意图是不是想显示content。如果手指移动的距离是负数，且当前menu是可见的，则认为当前手势是想要显示content。
     *
     * @return 当前手势想显示content返回true，否则返回false。
     */
    private boolean wantToShowContent() {
        return xUp - xDown < 0 && isMenuVisible;
    }

    /**
     * 判断当前手势的意图是不是想显示menu。如果手指移动的距离是正数，且当前menu是不可见的，则认为当前手势是想要显示menu。
     *
     * @return 当前手势想显示menu返回true，否则返回false。
     */
    private boolean wantToShowMenu() {
        return xUp - xDown > 0 && !isMenuVisible;
    }

    /**
     * 判断是否应该滚动将menu展示出来。如果手指移动距离大于屏幕的1/2，或者手指移动速度大于SNAP_VELOCITY，
     * 就认为应该滚动将menu展示出来。
     *
     * @return 如果应该滚动将menu展示出来返回true，否则返回false。
     */
    private boolean shouldScrollToMenu() {
        return xUp - xDown > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * 判断是否应该滚动将content展示出来。如果手指移动距离加上menuPadding大于屏幕的1/2，
     * 或者手指移动速度大于SNAP_VELOCITY， 就认为应该滚动将content展示出来。
     *
     * @return 如果应该滚动将content展示出来返回true，否则返回false。
     */
    private boolean shouldScrollToContent() {
        return xDown - xUp + menuPadding > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * 将屏幕滚动到menu界面，滚动速度设定为30.
     */
    private void scrollToMenu() {
        new ScrollTask().execute(30);
    }

    /**
     * 将屏幕滚动到content界面，滚动速度设定为-30.
     */
    private void scrollToContent() {
        new ScrollTask().execute(-30);
    }
    /**
     * 将屏幕滚动到content界面，滚动速度设定为-30.
     */
    private void scrollToContent(Integer speed) {
        new ScrollTask().execute(speed);
    }

    /**
     * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
     *
     * @param event
     *            content界面的滑动事件
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 获取手指在content界面滑动的速度。
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
// 如果是返回键,直接返回到桌面
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if (isMenuVisible) {
                scrollToContent(-130);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    class ScrollTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... speed) {
            int leftMargin = menuParams.leftMargin;
            // 根据传入的速度来滚动界面，当滚动到达左边界或右边界时，跳出循环。
            while (true) {
                leftMargin = leftMargin + speed[0];
                if (leftMargin > rightEdge) {
                    leftMargin = rightEdge;
                    break;
                }
                if (leftMargin < leftEdge) {
                    leftMargin = leftEdge;
                    break;
                }
                publishProgress(leftMargin);
                // 为了要有滚动效果产生，每次循环使线程睡眠20毫秒，这样肉眼才能够看到滚动动画。
                sleep(20);
            }
            if (speed[0] > 0) {
                isMenuVisible = true;
            } else {
                isMenuVisible = false;
            }
            return leftMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... leftMargin) {
            menuParams.leftMargin = leftMargin[0];
            menu.setLayoutParams(menuParams);
        }

        @Override
        protected void onPostExecute(Integer leftMargin) {
            menuParams.leftMargin = leftMargin;
            menu.setLayoutParams(menuParams);
        }
    }

    /**
     * 使当前线程睡眠指定的毫秒数。
     *
     * @param millis
     *            指定当前线程睡眠多久，以毫秒为单位
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
