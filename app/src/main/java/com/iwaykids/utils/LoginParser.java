package com.iwaykids.utils;

import android.util.Log;

import com.iwaykids.models.Login;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by SCORP on 14/07/15.
 */
public class LoginParser {

    public String getUserID(String data) {

        try {

            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject obj = jsonArray.getJSONObject(i);

                if (obj.getString("Status").equals("True"))
                    return obj.getString("user_id");
                else
                    return "False";

            }

        } catch (Exception e) {

            Log.e("LoginParserErr", e.toString());
        }

        return "";
    }
}
