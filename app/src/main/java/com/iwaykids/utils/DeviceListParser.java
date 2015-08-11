package com.iwaykids.utils;

import android.util.Log;

import com.iwaykids.models.Devices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by SCORP on 14/07/15.
 */
public class DeviceListParser {

    public ArrayList<Devices> getDevices(String data) {

        ArrayList<Devices> list = new ArrayList<>();

        try {

            JSONArray jsonArray = new JSONArray(data);

            Devices devices;

            for (int i = 0; i < jsonArray.length(); i++) {

                devices = new Devices();

                JSONObject obj = jsonArray.getJSONObject(i);

                devices.setId(obj.getString("deviceID"));
                devices.setDeviceName(obj.getString("device_name"));

                list.add(devices);
            }

            return list;

        } catch (Exception e) {

            Log.e("device sparser", e.toString());
        }

        return null;
    }
}
