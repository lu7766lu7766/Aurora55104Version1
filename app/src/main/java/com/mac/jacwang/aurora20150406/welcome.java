package com.mac.jacwang.aurora20150406;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

/**
 * Created by jac_note on 2015/4/6.
 */
public class welcome extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        new CountDownTimer(2500, 2500) {
            @Override
            public void onTick(long l) {
                //do notthing
            }
            @Override
            public void onFinish() {
                Intent intent = new Intent();
                intent.setClass(welcome.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
