package com.jjoe64.motiondetection;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

/**
 * Created by nina_malina on 19/05/18.
 */

public class CallAPI extends AsyncTask<String, String, String> {

    public CallAPI(){
        //set context variables if required
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }




    @Override
    protected String doInBackground(String... params) {

        String urlString = params[0]; // URL to call

        String weight = params[1]; //data to post
        String height = params[2]; //data to post
        String encodedImage = params[3]; //data to post

        String POST_PARAMS = "image=" + encodedImage;

        Log.d(TAG, urlString);

        OutputStream out = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");

            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(POST_PARAMS.getBytes());
            os.flush();
            os.close();
            //Log.d(TAG, encodedImage);


            int response = conn.getResponseCode();



//            conn.connect();

//            URL url = new URL(urlString);

//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");

//            out = new BufferedOutputStream(urlConnection.getOutputStream());
//
//            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));
//
//            writer.write(data);
//
//            writer.flush();
//
//            writer.close();
//
//            out.close();

//            urlConnection.connect();


        } catch (Exception e) {

            System.out.println(e.getMessage());



        }

        return urlString;
    }
}