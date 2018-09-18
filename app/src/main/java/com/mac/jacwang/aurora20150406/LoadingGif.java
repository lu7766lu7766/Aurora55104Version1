package com.mac.jacwang.aurora20150406;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

public class LoadingGif extends View{

    private Movie gifMovie;
    private InputStream gifinputStream;
    private int movieWidth,movieHeight;
    private long movieDuration;
    private long movieStart;

    int drawable = R.drawable.loading3;

    public LoadingGif(Context context) {
        super(context);
        init(context);
    }
    public LoadingGif(Context context, AttributeSet attr) {
        super(context,attr);
        init(context);
    }
    public LoadingGif(Context context, int drawable) {
        super(context);
        this.drawable = drawable;
        init(context);
    }
    public LoadingGif(Context context, AttributeSet attr, int defStyleAttr) {
        super(context,attr,defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);

        gifinputStream = context.getResources().openRawResource(+drawable);

        gifMovie = Movie.decodeStream(gifinputStream);
        movieWidth = gifMovie.width();
        movieHeight = gifMovie.height();
        movieDuration = gifMovie.duration();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(movieWidth,movieHeight);
    }

    public int getMovieWidth() {
        return movieWidth;
    }
    public int getMovieHeight() {
        return movieHeight;
    }
    public long getMovieDuration() {
        return movieDuration;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        long now = SystemClock.uptimeMillis();

        if(movieStart==0){
            movieStart = now;
        }

        if(gifMovie!=null){
            int dur = gifMovie.duration();
            if(dur==0)
                dur=1000;
            int relTime = ((int)(now-movieStart)%dur);
            gifMovie.setTime(relTime);
            gifMovie.draw(canvas,0,0);
            invalidate();
        }
    }
}
