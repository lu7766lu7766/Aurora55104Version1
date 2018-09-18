package com.mac.jacwang.aurora20150406;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jac on 2015/4/10.
 */
public class DBConnector {

    private static final String TAG = Static_var.TAG;

    String Url = Static_var.DB_CONNECT_PATH;

    Handler mHandler;
    public DBConnector(Handler handler,String sql,int type)
    {
        this.mHandler = handler;
        Thread t = new Thread(new sendPostRunnable(sql,type));
        t.start();
    }

    class sendPostRunnable implements Runnable
    {
        String strTxt = null;
        int type = 0;
        // 建構子，設定要傳的字串
        public sendPostRunnable(String strTxt,int type)
        {
            this.strTxt = strTxt;
            this.type = type;
        }

        @Override
        public void run()
        {
            String result = null;
            Bitmap bitmap = null;
            String lstrTxt = strTxt.toLowerCase();
            if(lstrTxt.indexOf("jpg")>-1||lstrTxt.indexOf("png")>-1||lstrTxt.indexOf("gif")>-1) {
                //bitmap =

            }else{
                result = sendPostDataToInternet(strTxt);
            }
            switch(type)
            {
                case 0:
                    mHandler.obtainMessage(Static_var.CITY_DATA, result).sendToTarget();
                    break;
                case 1:
                    mHandler.obtainMessage(Static_var.DIST_DATA, result).sendToTarget();
                    break;
                case 2:
                    mHandler.obtainMessage(Static_var.STORE_DATA, result).sendToTarget();
                    break;
                case 3:
                    mHandler.obtainMessage(Static_var.SETIMAGE_DATA, result).sendToTarget();
                    break;
                case 4:
                    mHandler.obtainMessage(Static_var.BRAND_DATA, result).sendToTarget();
                    break;
                case 5:
                    mHandler.obtainMessage(Static_var.TYPE_DATA, result).sendToTarget();
                    break;
            }
        }
    }
    private String sendPostDataToInternet(String strTxt)
    {

        String[] parse = strTxt.split(",");
        String table = parse[0];
        String type  = parse[1];
        String brand = parse[2];
        String city  = parse[3];
        String dist  = parse[4];
        String lat   = parse[5];
        String lng   = parse[6];
        String page  = parse[7];
        String SyncURL=Url;
        String response;
        HttpPost hp = new HttpPost(SyncURL);
        HttpResponse hr;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //Add Post Data
        params.add(new BasicNameValuePair("table",table));
        params.add(new BasicNameValuePair("type" ,type));
        params.add(new BasicNameValuePair("brand",brand));
        params.add(new BasicNameValuePair("city" ,city));
        params.add(new BasicNameValuePair("dist" ,dist));
        params.add(new BasicNameValuePair("lat"  ,lat));
        params.add(new BasicNameValuePair("lng"  ,lng));
        params.add(new BasicNameValuePair("page" ,page));
        try {
            UrlEncodedFormEntity urf = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            hp.setEntity(urf);
            hr = new DefaultHttpClient().execute(hp);
            if(hr.getStatusLine().getStatusCode()==200){
                response= EntityUtils.toString(hr.getEntity());
                return response;
            }else{
                Log.i(TAG,"can't connect~");
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            return new String( "UnsupportedEncodingException"+e.getMessage());
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            return new String( "ClientProtocolException"+e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return new String( "IOException"+e.getMessage());
        }
//        try{
//            String link = Url;
//            String data  = "table="  + URLEncoder.encode(table, "UTF-8") +
//                           "&type="  + URLEncoder.encode(type,  "UTF-8") +
//                           "&brand=" + URLEncoder.encode(brand, "UTF-8") +
//                           "&city="  + URLEncoder.encode(city,  "UTF-8") +
//                           "&dist="  + URLEncoder.encode(dist,  "UTF-8") +
//                           "&lat="   + URLEncoder.encode(lat,   "UTF-8") +
//                           "&lng="   + URLEncoder.encode(lng,   "UTF-8") +
//                           "&page="  + URLEncoder.encode(page,  "UTF-8") ;
//            URL url = new URL(link);
//            Log.i(TAG,"link:"+link);
//            URLConnection conn = url.openConnection();
//            conn.setDoOutput(true);
//            OutputStreamWriter wr = new OutputStreamWriter
//                    (conn.getOutputStream());
//            wr.write( data );
//            wr.flush();
//            BufferedReader reader = new BufferedReader
//                    (new InputStreamReader(conn.getInputStream()));
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            // Read Server Response
//            while((line = reader.readLine()) != null)
//            {
//                Log.i(TAG,"line:"+line);
//                sb.append(line);
//                break;
//            }
//            return sb.toString();
//        }catch(Exception e){
//            return new String("Exception: " + e.getMessage());
//        }
    }
}
