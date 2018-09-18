package com.mac.jacwang.aurora20150406;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class Pos_selector extends Service implements View.OnClickListener {

    private static final String TAG = Static_var.TAG;

    static View pos_selector;
    WindowManager wm;
    WindowManager.LayoutParams params;
    final int shrink_width = 15;
    final int source_width = 280;

    Resources r;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate(){
        Log.i(TAG, "pos_selector show!!");
        super.onCreate();

        r = getResources();

        wm = (WindowManager) getApplicationContext().
                getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = getPx(shrink_width);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //params.gravity= Gravity.RIGHT|Gravity.TOP;

        // Calculate ActionBar height

        LayoutInflater mInflater = LayoutInflater.from(this);
        pos_selector = mInflater.inflate(R.layout.pos_selector, null);

        Button pos_cancel = (Button)pos_selector.findViewById(R.id.pos_cancel);
        pos_cancel.setOnClickListener(this);

        Button pos_enter = (Button)pos_selector.findViewById(R.id.pos_enter);
        pos_enter.setOnClickListener(this);



        wm.addView(pos_selector, params);

    }

    @Override
    public void onDestroy() {
        WindowManager wm = (WindowManager) getApplicationContext().
        getSystemService(Context.WINDOW_SERVICE);
        if (pos_selector != null) {
            wm.removeView(pos_selector);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.pos_cancel:
                break;
            case R.id.pos_enter:
                break;
        }
    }

    private int getPx(int dp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}