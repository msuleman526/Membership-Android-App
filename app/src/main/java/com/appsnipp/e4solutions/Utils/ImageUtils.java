package com.appsnipp.e4solutions.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtils {

    public interface OnImageLoadedListener {
        void onImageLoaded(String base64EncodedImage);
        void onError(Exception e);
    }

    public static void loadImageAsBase64(final String imageUrl, final OnImageLoadedListener listener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                    byte[] b = baos.toByteArray();
                    String encImage = Base64.encodeToString(b, Base64.DEFAULT);
                    return encImage;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String base64EncodedImage) {
                if (base64EncodedImage != null) {
                    listener.onImageLoaded(base64EncodedImage);
                } else {
                    listener.onError(new Exception("Failed to load image"));
                }
            }
        }.execute();
    }
}