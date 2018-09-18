package com.mac.jacwang.aurora20150406;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CallImage extends ImageView {

    private String call="";
    private int index=0;
    private int i=0;
    private String menu="";

    public CallImage(Context context){
        super(context);
    }

    public CallImage(Context context, AttributeSet attrs){
        super(context, attrs);
        //从attrs.xml中加载一个名字叫’ .MRadioButton’的declare-styleable资源
        TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.CallImage);
        //将属性value与类中的属性call关联
        this.call = tArray.getString(R.styleable.CallImage_call);
        //回收tArray对象
        tArray.recycle();
    }

    public CallImage(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public String getCall() {
        return call;
    }
    public void setCall(String mCall) {
        this.call = mCall;
    }

    public int getIndex() { return index;}
    public void setIndex(int mIndex) {
        this.index = mIndex;
    }

    public int getI(){return i; }
    public void setI(int mI){ this.i = mI; }

    public String getMenu(){return menu; }
    public void setMenu(String menu){ this.menu = menu; }
}
