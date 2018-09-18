package com.mac.jacwang.aurora20150406;
import android.app.ActionBar;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PosBrand_selector extends Service implements View.OnClickListener {

    private static final String TAG = Static_var.TAG;

    View pos_brand_selector,pos_selector,brand_selector;
    LayoutInflater mInflater;
    WindowManager wm;
    WindowManager.LayoutParams params;
    WindowManager.LayoutParams pos_params;
    WindowManager.LayoutParams brand_params;
    final int shrink_width = 15;
    final int source_width = 280;
    final int pos_width = 270;
    final int pos_height = 370;

    float brand_text_size;
    Resources r;

    TextView area_val;
    TextView brand_val;

    Button area_btn,brand_btn;
    ImageView search_btn;

    final String sarea_txt = "以使用者為中心";
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        r = getResources();

        wm = (WindowManager) getApplicationContext().
                getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = getPx(shrink_width);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity= Gravity.RIGHT|Gravity.TOP;

        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            params.y = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics())+getPx(15);
        }
        mInflater = LayoutInflater.from(this);
        pos_brand_selector = mInflater.inflate(R.layout.pos_brand_selector, null);

        Button pos_brand_selector_btn = (Button)pos_brand_selector.findViewById(R.id.show_btn);
        pos_brand_selector_btn.setOnClickListener(this);

        area_btn = (Button)pos_brand_selector.findViewById(R.id.area_btn);
        area_btn.setText(sarea_txt);
        area_btn.setOnClickListener(this);

        brand_btn = (Button)pos_brand_selector.findViewById(R.id.brand_btn);
        brand_btn.setOnClickListener(this);

        search_btn = (ImageView)pos_brand_selector.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(this);

        pos_brand_selector.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //x = event.getRawX();
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //startX = event.getRawX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //updatePosition();
                        break;
                    case MotionEvent.ACTION_UP:
                        params.width = getPx(shrink_width);
                        wm.updateViewLayout(pos_brand_selector, params);
                        //startX = startY = 0;
                        break;
                    }
                return true;
            }
        });
        wm.addView(pos_brand_selector, params);

    }

    @Override
    public void onDestroy() {
        //WindowManager wm = (WindowManager) getApplicationContext().
        //getSystemService(Context.WINDOW_SERVICE);
        if (pos_selector != null) {
            wm.removeView(pos_selector);
        }
        if (brand_selector != null) {
            wm.removeView(brand_selector);
        }
        if (pos_brand_selector != null) {
            wm.removeView(pos_brand_selector);
        }
        wm = null;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.show_btn:
                params.width = getPx(source_width);
                wm.updateViewLayout(pos_brand_selector, params);
                //Log.i(TAG,"pos_brand_selector_btn click!!");
                break;
            case R.id.area_btn:
                pos_params = new WindowManager.LayoutParams();
                pos_params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                pos_params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                pos_params.width = getPx(pos_width);
                pos_params.height = getPx(pos_height);
                pos_selector = mInflater.inflate(R.layout.pos_selector, null);
                area_val = (TextView)pos_selector.findViewById(R.id.area_val);
                area_val.setText("");
                Button pos_cancel = (Button)pos_selector.findViewById(R.id.pos_cancel);
                pos_cancel.setOnClickListener(this);
                Button pos_enter = (Button)pos_selector.findViewById(R.id.pos_enter);
                pos_enter.setEnabled(false);
                pos_enter.setOnClickListener(this);
                wm.addView(pos_selector, pos_params);
                new DBConnector(mHandler,"city,null,null,null,null,null,null,null",0);

                break;
            case R.id.brand_btn:
                brand_params = new WindowManager.LayoutParams();
                brand_params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                brand_params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                brand_params.width = getPx(pos_width);
                brand_params.height = getPx(pos_height);
                brand_selector = mInflater.inflate(R.layout.brand_selector, null);
                brand_val = (TextView)brand_selector.findViewById(R.id.brand_val);
                brand_text_size = brand_val.getTextSize();
                brand_val.setText("");
                Button brand_enter = (Button)brand_selector.findViewById(R.id.brand_enter);
                brand_enter.setOnClickListener(this);
                Button brand_cancel = (Button)brand_selector.findViewById(R.id.brand_cancel);
                brand_cancel.setOnClickListener(this);
                wm.addView(brand_selector, brand_params);
                new DBConnector(mHandler,"brand,"+Static_var.search_type+",null,null,null,null,null,null",4);
                break;
            case R.id.search_btn:
                Intent intent = new Intent("my-event");
                // add data
                intent.putExtra("city", city_data);
                intent.putExtra("dist", dist_data);
                intent.putExtra("brand",brand_data.equals("不限")?"null":brand_data);
                //Log.i(TAG,"send data");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                params.width = getPx(shrink_width);
                wm.updateViewLayout(pos_brand_selector, params);
                break;
            
            ///////////////////////////////////////////////////////////////////////
            case R.id.pos_enter:
                area_btn.setText((String) area_val.getText());
                if(pos_selector!=null) {
                    wm.removeView(pos_selector);
                    pos_selector = null;
                }
                break;
            case R.id.pos_cancel:
                pos_enter = (Button)pos_selector.findViewById(R.id.pos_enter);
                if(pos_enter.isEnabled())
                {
                    pos_enter.setEnabled(false);
                    dist_data = tmp_dist_data;
                    new DBConnector(mHandler,"city,null,null,null,null,null,null,null",0);
                    //Log.i(TAG,""+pos_enter.isEnabled());
                }
                else
                {
                    city_data = tmp_city_data;
                    dist_data = tmp_dist_data;
                    if(pos_selector!=null) {
                        wm.removeView(pos_selector);
                        pos_selector = null;
                    }
                    //Log.i(TAG,""+pos_enter.isEnabled());
                }
                break;
            ///////////////////////////////////////////////////////////////
            case R.id.brand_enter:
                brand_btn.setText((String)brand_val.getText());
                if(brand_selector!=null){
                    wm.removeView(brand_selector);
                    brand_selector = null;
                }

                break;
            case R.id.brand_cancel:
                if(brand_selector!=null){
                    wm.removeView(brand_selector);
                    brand_selector = null;
                }
                break;
        }
    }

    public int getPx(int dp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            String result = null;
            if(msg.obj!=null){
                if (msg.obj instanceof String)
                    result = (String) msg.obj;
                switch (msg.what)
                {
                    case Static_var.CITY_DATA:

                        List<String> city_list = analysis_city(result);
                        //Log.i(TAG, "city_data : "+city_list.toString());
                        setCityData(city_list);
                    break;
                    case Static_var.DIST_DATA:
                        List<String> dist_list = analysis_dist(result);
                        setDistData(dist_list);
                        break;
                    case Static_var.BRAND_DATA:
                        List<String> brand_list = analysis_brand(result);
                        Log.i(TAG,brand_list.toString());
                        setBrandData(brand_list);
                        break;
                }
            }
        }
    };

    private List<String> analysis_city(String result){
        List<String> city_list = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                city_list.add(jsonData.getString("fv_city"));
            }
        } catch(Exception e) {
            Log.e(TAG, e.toString());
        }
        return city_list;
    }
    private List<String> analysis_dist(String result){
        List<String> dist_list = new ArrayList<String>();
        Log.e(TAG, "dist_result"+result);
        try {
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                dist_list.add(jsonData.getString("fv_dist"));
            }
        } catch(Exception e) {
            Log.e(TAG, e.toString());
        }
        return dist_list;
    }
    private List<String> analysis_brand(String result){
        List<String> brand_list = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                brand_list.add(jsonData.getString("fv_brand_name"));
            }
        } catch(Exception e) {
            Log.e(TAG, e.toString());
        }
        return brand_list;
    }

    String city_data="",tmp_city_data="";
    Button[] city_btns;
    String btn_click_color = "#416236";
    String btn_source_color = "#00000000";
    private void setCityData(List<String> city_list) {

        int column_len = 3;

        ScrollView pos_scroll = (ScrollView) pos_selector.findViewById(R.id.pos_scroll);
        pos_scroll.removeAllViews();

        TableLayout container = new TableLayout(this);

        TableRow line_container = null;

        line_container = new TableRow(this);

        TableRow.LayoutParams btn_params =
                new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        btn_params.column = 0;
        btn_params.span = 3;
        Button city_btn = new Button(this);
        city_btn.setLayoutParams(btn_params);
        city_btn.setGravity(Gravity.CENTER_HORIZONTAL);
        city_btn.setBackgroundColor(Color.parseColor(btn_source_color));
        city_btn.setTextSize(20);
        city_btn.setText(sarea_txt);
        city_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                area_val.setText(sarea_txt);
                city_data = "null";
                dist_data = "null";
                Button pos_enter = (Button)pos_selector.findViewById(R.id.pos_enter);
                pos_enter.performClick();
            }
        });
        line_container.addView(city_btn);
        container.addView(line_container);

        city_btns = new Button[city_list.size()];
        for (int i = 0; i < city_list.size(); i++)
        {
            btn_params =
                    new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
            btn_params.column = i%column_len;

            city_btn = new Button(this);
            city_btn.setLayoutParams(btn_params);
            city_btn.setGravity(Gravity.CENTER_HORIZONTAL);
            city_btn.setBackgroundColor(Color.parseColor(btn_source_color));
            city_btn.setTextSize(20);
            city_btn.setText(city_list.get(i));
            city_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    city_data = (String)((Button)v).getText();
                    dist_data = "null";
                    //Log.i(TAG,"new val : " + city_data);
                    area_val.setText(city_data);
                    ((Button)v).setBackgroundColor(Color.parseColor(btn_click_color));
                    new DBConnector(mHandler,"dist,null,null,"+city_data+",null,null,null,null",1);
                    Button pos_enter = (Button)pos_selector.findViewById(R.id.pos_enter);
                    pos_enter.setEnabled(true);
                }
            });
            if(i%column_len==0){
                line_container = new TableRow(this);
                container.addView(line_container);
            }
            line_container.addView(city_btn);
            city_btns[i] = city_btn;
        }
        pos_scroll.addView(container);
    }

    String dist_data="",tmp_dist_data="";
    Button[] dist_btns;
    private void setDistData(List<String> dist_list){

        int column_len = 3;//列數

        ScrollView pos_scroll = (ScrollView)pos_selector.findViewById(R.id.pos_scroll);
        pos_scroll.removeAllViews();

        TableLayout container = new TableLayout(this);

        TableRow line_container = null;
        dist_btns = new Button[dist_list.size()];
        for (int i = 0; i < dist_list.size(); i++)
        {
            Button dist_btn = new Button(this);
            TableRow.LayoutParams btn_params =
                    new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
            btn_params.column = i%column_len;

            dist_btn.setLayoutParams(btn_params);
            dist_btn.setGravity(Gravity.CENTER_HORIZONTAL);
            dist_btn.setBackgroundColor(Color.parseColor(btn_source_color));
            dist_btn.setTextSize(20);
            dist_btn.setText(dist_list.get(i));
            dist_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int len = dist_btns.length;
                    if(!dist_data.isEmpty())
                        for (int i = 0; i < len; i++)
                        {
                            if(dist_btns[i].getText()==dist_data)
                            {
                                dist_btns[i].setBackgroundColor(Color.parseColor(btn_source_color));
                                //Log.i(TAG,"old val : " + (String)city_btns[i].getText());
                                break;
                            }
                        }
                    dist_data = (String)((Button)v).getText();
                    //Log.i(TAG,"new val : " + city_data);
                    area_val.setText(city_data+" "+dist_data);
                    ((Button)v).setBackgroundColor(Color.parseColor(btn_click_color));
                }
            });
            if(i%column_len==0){
                line_container = new TableRow(this);
                container.addView(line_container);
            }
            line_container.addView(dist_btn);
            dist_btns[i] = dist_btn;
        }
        pos_scroll.addView(container);
    }

    String brand_data="";
    Button[] brand_btns;
    private void setBrandData(List<String> brand_list){

        int column_len = 1;//列數

        ScrollView brand_scroll = (ScrollView)brand_selector.findViewById(R.id.brand_scroll);
        brand_scroll.removeAllViews();

        TableLayout container = new TableLayout(this);

        TableRow line_container = new TableRow(this);

        brand_btns = new Button[brand_list.size()+1];

        TableRow.LayoutParams btn_params =
                new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        btn_params.column = 0;

        Button brand_btn = new Button(this);
        brand_btn.setLayoutParams(btn_params);
        brand_btn.setText("不限");
        brand_btns[0]=brand_btn;


        line_container.setGravity(Gravity.CENTER_HORIZONTAL);
        line_container.addView(brand_btn,btn_params);
        container.addView(line_container);

        for (int i = 1; i <= brand_list.size(); i++)
        {
            btn_params =
                    new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
            btn_params.column = i%column_len;

            brand_btn = new Button(this);
            brand_btn.setLayoutParams(btn_params);
            brand_btn.setText(brand_list.get(i-1));
            brand_btns[i] = brand_btn;

            if(i%column_len==0){
                line_container = new TableRow(this);
                line_container.setGravity(Gravity.CENTER_HORIZONTAL);
                container.addView(line_container);
            }
            line_container.addView(brand_btn,btn_params);

        }

        int len = brand_btns.length;
        for (int i = 0; i < len ; i++)
        {
            brand_btns[i].setBackgroundColor(Color.parseColor(btn_source_color));
            brand_btns[i].setTextSize(20);
            brand_btns[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int len = brand_btns.length;
                    if(!brand_data.isEmpty())
                        for (int i = 0; i < len; i++)
                        {
                            if(brand_btns[i].getText()==brand_data)
                            {
                                brand_btns[i].setBackgroundColor(Color.parseColor(btn_source_color));
                                break;
                            }
                        }
                    brand_data = (String)((Button)v).getText();
                    brand_val.setText(brand_data);
                    brand_val.setTextSize(TypedValue.COMPLEX_UNIT_PX, brand_text_size);
                    brand_val.post(new Runnable() {
                        @Override
                        public void run() {
                            float textSize = brand_val.getTextSize();
                            while ((brand_val.getWidth() - getPx(60)) < brand_val.getText().toString().length() * textSize--)
                                ;
                            brand_val.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                        }
                    });
                    ((Button)v).setBackgroundColor(Color.parseColor(btn_click_color));
                }
            });
        }

        brand_scroll.addView(container);
    }
}