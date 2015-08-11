package com.iwaykids.services;

import android.util.Log;

import com.iwaykids.models.Response;
import com.iwaykids.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by SCORP on 19/05/15.
 */
public class MapDataParser {

    JSONObject mainObj;
    ArrayList<Response> responseList;

    public ArrayList<Response> parse(String jsonStr) {

        try {

            if(jsonStr.equals("Data Not Available.")){

                return null;

            }else {


                responseList = new ArrayList<>();

                JSONArray mainArray = new JSONArray(jsonStr);

                for (int i = 0; i < mainArray.length(); i++) {

                    Response response = new Response();


                    JSONObject dataObj = mainArray.getJSONObject(i);

//                if(i==mainArray.length()-1){
//
//                    response.setBetteryStatus(dataObj.getString(Constants.BETTERY));
//                    response.setSignal(dataObj.getString(Constants.SIGNAL));
//
//                    Log.i("bettery",dataObj.getString(Constants.BETTERY));
//                }
//                else {

                    response.setLat(dataObj.getString(Constants.LAT));
                    response.setLng(dataObj.getString(Constants.LNG));
                    response.setSpeed(dataObj.getString(Constants.SPEED));
                    response.setAlt(dataObj.getString(Constants.ALTITUDE));
                    response.setDate(dataObj.getString(Constants.DATE));
                    Log.i("address", dataObj.getString(Constants.ADDRESS));
                    response.setAddress(dataObj.getString(Constants.ADDRESS));

//                }

                    responseList.add(response);


                }


                return responseList;
            }


        } catch (Exception e) {

            Log.i("Mapdata Parser Error", e.toString());
        }


        return responseList;
    }

}