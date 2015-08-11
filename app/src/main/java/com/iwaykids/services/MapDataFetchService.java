package com.iwaykids.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.iwaykids.activities.MapActivity;
import com.iwaykids.utils.Constants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SCORP on 19/05/15.
 */
public class MapDataFetchService extends IntentService {


    String data="";
    String str="";
    String params="";
    String action="";

    private URL url;
    private HttpURLConnection connection;
    private DataOutputStream outputStream;
    private BufferedReader reader;


    public MapDataFetchService(){

        super("MapDataFetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        InputStream is;
        SharedPreferences preferences = getSharedPreferences(Constants.APP_PREF_NAME,MODE_PRIVATE);

        try{

            String u = intent.getStringExtra(Constants.URL)+preferences.getString(Constants.SELECTED_DEVICE_ID,"");
            Log.i("url",intent.getStringExtra(Constants.URL)+preferences.getString(Constants.SELECTED_DEVICE_ID,""));

            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int responseCode = conn.getResponseCode();
            Log.d(Constants.RESPONSE_CODE, "The response is: " + responseCode);

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((str = reader.readLine()) != null) {
                data = str;
            }

            Log.i("map data response",data);


            Intent response = new Intent(this, MapActivity.MapDataRecieved.class);
            response.setAction(MapActivity.MapDataRecieved.ACTION_MAPDATA_FETCHED);
            response.addCategory(Intent.CATEGORY_DEFAULT);
            response.putExtra(Constants.RESPONSE,data);

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(response);


        }catch (Exception e){
            Log.e("Error Map Data Service","Error: "+e.toString());
        }

    }
}
