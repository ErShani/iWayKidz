package com.iwaykids.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.iwaykids.activities.LoginActivity;
import com.iwaykids.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SCORP on 14/07/15.
 */
public class LoginService extends IntentService {

    BufferedReader reader;
    String data;
    String str;

    public LoginService() {
        super("LoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        InputStream is = null;

        try {
            URL url = new URL(intent.getStringExtra(Constants.LOGIN_URL));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();
            Log.d(Constants.RESPONSE_CODE, "The response is: " + response);

            is = conn.getInputStream();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((str = reader.readLine()) != null) {
                data = str;
            }

            Log.i("login response", data);

            is.close();
            reader.close();

            Intent responseIntent = new Intent(this, LoginActivity.LoginDone.class);
            responseIntent.setAction(LoginActivity.LoginDone.ACTION_LOGIN_DONE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(Constants.RESPONSE, data);

            LocalBroadcastManager.getInstance(this).sendBroadcast(responseIntent);


        } catch (Exception e) {

            Log.e("Login Err", e.toString());
        }
    }

}
