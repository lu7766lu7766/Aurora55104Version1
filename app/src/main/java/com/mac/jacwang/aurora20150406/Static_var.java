package com.mac.jacwang.aurora20150406;

import android.location.Location;
import android.os.Environment;

import java.io.File;

/**
 * Created by jac on 2015/4/10.
 */
public class Static_var {

    protected static final String TAG = "jac:";
    protected static final int CITY_DATA = 0x00000000;
    protected static final int DIST_DATA = 0x00000001;
    protected static final int BRAND_DATA = 0x00000004;
    protected static final int STORE_DATA = 0x00000002;
    protected static final int SETIMAGE_DATA = 0x00000003;
    protected static final int TYPE_DATA = 0x00000005;
    protected static int search_type = 1;//預設種類--1飲料
    protected static String search_brand = "null";//預設品牌
    protected static String search_city = "null";//預設品牌
    protected static String search_dist = "null";//預設品牌
    //protected static final String HOST = "http://27.105.182.18:8080/aurora01/";
    //protected static final String HOST = "http://125.227.84.248:8080/aurora01/";
    protected static final String HOST = "http://app.55104.com.tw:8080/";
    protected static final String LIBRARY_PATH = "library/";
    protected static final String IMAGE_PATH = "images/";
    protected static final String BRANDLOGO_PATH = HOST+IMAGE_PATH+"brand_logo/";
    protected static final String BRANDMENU_PATH = HOST+IMAGE_PATH+"brand_menu/";
    protected static final String TYPELOGO_PATH = HOST+IMAGE_PATH+"store_type/";
    protected static final String LOCAL_PATH = "data/55104/";
    protected static final String DB_CONNECT_PATH = HOST+LIBRARY_PATH+"android_connect_db.php";
    protected static final String APP_PATH = Environment.getExternalStorageDirectory()+ File.separator + LOCAL_PATH;
    protected static Location location = null;
}
