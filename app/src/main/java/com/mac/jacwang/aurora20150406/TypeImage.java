package com.mac.jacwang.aurora20150406;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TypeImage extends ImageView {

    private int index;

    public TypeImage(Context context){
        super(context);
    }

    public TypeImage(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public TypeImage(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public int getIndex() { return index;}
    public void setIndex(int mIndex) {
        this.index = mIndex;
    }
}
