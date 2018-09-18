package com.mac.jacwang.aurora20150406;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class LazyScrollView extends ScrollView{

    private static final String tag="LazyScrollView";
    private Handler handler;
    private View view;
    private int under_height = 0;
    private float xDistance, yDistance, xLast, yLast;

    public LazyScrollView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public void set_under_height(int under_height){
        this.under_height = under_height;
    }
    public int get_under_height(){return under_height;}

    public LazyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    public LazyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
    //這個獲得總的高度
    public int computeVerticalScrollRange(){
        return super.computeHorizontalScrollRange();
    }
    public int computeVerticalScrollOffset(){
        return super.computeVerticalScrollOffset();
    }
    private void init(){

        this.setOnTouchListener(onTouchListener);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // process incoming messages here
                super.handleMessage(msg);
                switch(msg.what){
                    case 1:
                        if(view.getMeasuredHeight() <= getScrollY() + getHeight() + under_height) {
                            if(onScrollListener!=null){
                                onScrollListener.onBottom();
                            }

                        }else if(getScrollY()==0){
                            if(onScrollListener!=null){
                                //onScrollListener.onTop();
                            }
                        }
                        else{
                            if(onScrollListener!=null){
                                //onScrollListener.onScroll();
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };

    }

    OnTouchListener onTouchListener=new OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    if(view!=null&&onScrollListener!=null){
                        handler.sendMessageDelayed(handler.obtainMessage(1), 200);
                    }
                    break;

                default:
                    break;
            }
            return false;
        }

    };

    /**
     * 獲得参考的View，主要是为了獲得它的MeasuredHeight，然後和滾動條的ScrollY+getHeight作比較。
     */
    public void getView(){
        this.view=getChildAt(0);
        if(view!=null){
            init();
        }
    }

    /**
     * 定義接口
     * @author admin
     *
     */
    public interface OnScrollListener{
        void onBottom();
        //void onTop();
        //void onScroll();
    }
    private OnScrollListener onScrollListener;
    public void setOnScrollListener(OnScrollListener onScrollListener){
        this.onScrollListener=onScrollListener;
    }

}