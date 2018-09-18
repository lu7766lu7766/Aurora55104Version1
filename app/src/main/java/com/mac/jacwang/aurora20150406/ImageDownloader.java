package com.mac.jacwang.aurora20150406;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader extends AsyncTask<Void, Integer, Void> {

    private String url,title;
    private ImageView img;
    private Bitmap bmp;
    private Context c;
    private ImageLoaderListener listener;

    /*--- constructor ---*/
    public ImageDownloader(String url, String title, Context c,
                           ImageView img, ImageLoaderListener listener) {

        File fpath = new File(Static_var.APP_PATH);
        if (!fpath.isDirectory()) {
            fpath.mkdirs();
        }

        this.url = url;
        this.title = title;
        this.img = img;
        this.c = c;
        this.listener = listener;
    }

    public interface ImageLoaderListener {

        void onImageDownloaded(ImageView img,Bitmap bmp);

    }

    @Override
    protected void onPreExecute() {
        if(fileExists(title)){
            InputStream bitmap= null;
            try {
                bitmap = new FileInputStream(Static_var.APP_PATH + title);
                Bitmap bit=BitmapFactory.decodeStream(bitmap);
                img.setImageBitmap(bit);
                bitmap.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            super.onPreExecute();
        }
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        bmp = getBitmapFromURL(url);
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {
        //Log.i("jac:","img Loaded!!");
        if (listener != null) {
            listener.onImageDownloaded(img,bmp);
        }
        if(bmp!=null)
            img.setImageBitmap(bmp);
        //save.setEnabled(true);
        saveImageToSD();
       // Toast.makeText(c, "download complete", Toast.LENGTH_SHORT).show();

        super.onPostExecute(result);
    }

    public static Bitmap getBitmapFromURL(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("getBmpFromUrl error: ", e.getMessage().toString());
            return null;
        }
    }

    private FileOutputStream fos;
    private void saveImageToSD() {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if(bytes.size()>0) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //Log.i("jac:","cent to sd card:"+bytes);
            File file = new File(Static_var.APP_PATH + title);
            //Log.i("jac:","file path:"+file.getPath());
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    fos = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fos.write(bytes.toByteArray());
                    fos.close();
                    //Toast.makeText(c, "Image saved", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean fileExists(String filename) {
        boolean bAssetOk = false;
        try {
            InputStream stream = new FileInputStream(Static_var.APP_PATH + filename);
            stream.close();
            bAssetOk = true;
        } catch (FileNotFoundException e) {
            Log.w("IOUtilities", "assetExists failed: "+e.toString());
        } catch (IOException e) {
            Log.w("IOUtilities", "assetExists failed: "+e.toString());
        }
        return bAssetOk;
    }
}