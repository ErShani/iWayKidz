package com.iwaykids.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iwaykids.R;
import com.iwaykids.adapter.DeviceNamesAdapter;
import com.iwaykids.models.Devices;
import com.iwaykids.models.Response;
import com.iwaykids.services.ConnectionDetector;
import com.iwaykids.services.DeviceListFetcherService;
import com.iwaykids.services.MapDataFetchService;
import com.iwaykids.services.MapDataParser;
import com.iwaykids.utils.Constants;
import com.iwaykids.utils.DeviceListParser;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends AppCompatActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    ProgressDialog dialog;
    TextView tvBettery, tvSignal;
    ImageView ivSignal;
    ImageView ivBettery;
    Timer timer;
    TextView tvAddress;
    SharedPreferences pref;
    ArrayList<Devices> list;
    Spinner spDeviceName;
    public ProgressDialog deviceListProgressDialog;
    Button btnLogout;
    ProgressDialog mapDataDialog;
    Typeface tfOpenRegular;
    CardView map_status_bar;
    TextView tvSpeed;
    Handler handler;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent alarmIntent = new Intent(this, MapDataFetchService.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = 8000;
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);


        pref = getSharedPreferences(Constants.APP_PREF_NAME, MODE_PRIVATE);
        handler = new Handler();
        tfOpenRegular = Typeface.createFromAsset(getAssets(), "normal.ttf");

        IntentFilter mapDataIntentFilter = new IntentFilter(MapDataRecieved.ACTION_MAPDATA_FETCHED);
        mapDataIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(MapActivity.this);
        manager.registerReceiver(new MapDataRecieved(), mapDataIntentFilter);

        if (pref.getBoolean(Constants.USER_LOGIN_STATUS, false) == false) {

            finish();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);

        } else {

            IntentFilter filter = new IntentFilter(DeviceListReady.ACTION_DEVICE_LIST_READY);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            LocalBroadcastManager.getInstance(this).registerReceiver(new DeviceListReady(), filter);

            tvAddress = (TextView) findViewById(R.id.tvAddress);
            tvAddress.setTypeface(tfOpenRegular);

            map_status_bar = (CardView) findViewById(R.id.map_status_bar);
            tvSpeed = (TextView) findViewById(R.id.tvSpeed);


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            spDeviceName = (Spinner) toolbar.findViewById(R.id.spnr_device);

            btnLogout = (Button) toolbar.findViewById(R.id.btn_logout);
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                    builder.setMessage("Are you sure you want to Logout?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                            pref.edit().putBoolean(Constants.USER_LOGIN_STATUS, false).commit();
                            pref.edit().putString(Constants.USERID, "").commit();
                            Intent loginIntent = new Intent(MapActivity.this, LoginActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginIntent);

                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                        }
                    });
                    builder.show();

                }
            });

            if (ConnectionDetector.isNetworkAvailable(this) == false) {

                final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "NO INTERNET CONNECTION", Snackbar.LENGTH_LONG);
                snackbar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        snackbar.dismiss();
                    }
                })
                        .setActionTextColor(Color.RED)
                        .show();
            } else {

                list = new ArrayList<>();

                if (ConnectionDetector.isNetworkAvailable(MapActivity.this)) {


                    deviceListProgressDialog = new ProgressDialog(MapActivity.this);
                    deviceListProgressDialog.setMessage("Please Wait..");
                    deviceListProgressDialog.setCancelable(false);
                    deviceListProgressDialog.show();

                    Intent deviceFetcher = new Intent(MapActivity.this, DeviceListFetcherService.class);
                    deviceFetcher.setAction(DeviceListReady.ACTION_DEVICE_LIST_READY);
                    deviceFetcher.addCategory(Intent.CATEGORY_DEFAULT);
                    deviceFetcher.putExtra(Constants.DEVICE_LIST_URL, Constants.BASE_URL + "/get_devicelist.php?user_id=" + pref.getString(Constants.USERID, ""));
                    startService(deviceFetcher);


                } else {


                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Internet Connection is not Available")
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();
                                }
                            });


                }


            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
//            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        }
    }


    private void setUpMap(ArrayList<Response> mapDataList) {

        mMap.clear();

        for (int i = 0; i < mapDataList.size(); i++) {

            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(mapDataList.get(i).getLat()), Double.parseDouble(mapDataList.get(i).getLng()))).title(""));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mapDataList.get(i).getLat()), Double.parseDouble(mapDataList.get(i).getLng())), 17.0f));

            tvAddress.setText(mapDataList.get(i).getAddress());
        }
    }

    public class MapDataRecieved extends BroadcastReceiver {

        public static final String ACTION_MAPDATA_FETCHED = "com.iwaykids.mapdatarecieved";
        private static final long REPEAT_TIME = 1000 * 30;

        @Override
        public void onReceive(Context context, Intent intent) {

//
            String dataJson = intent.getStringExtra(Constants.RESPONSE);
            mapDataDialog.dismiss();

            MapDataParser parser = new MapDataParser();
            if (parser.parse(dataJson) == null) {

                final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "DATA NOT AVAILABLE", Snackbar.LENGTH_LONG);
                snackbar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        snackbar.dismiss();
                    }
                })
                        .setActionTextColor(Color.RED)
                        .show();

                map_status_bar.setVisibility(View.VISIBLE);
                mMap.clear();
                tvSpeed.setText("");
                tvAddress.setText("Data not available");

            } else {


                try {
                    map_status_bar.setVisibility(View.VISIBLE);
                    Double speed = Double.parseDouble(parser.parse(dataJson).get(0).getSpeed().toString());
                    tvSpeed.setText( ""+Math.round(speed)+" Km/Hr." + "  Time: "+ " "+parser.parse(dataJson).get(0).getDate());
                    setUpMap(parser.parse(dataJson));
                } catch (Exception e) {

                    Log.e("decimal format err", e.toString());
                }
            }



            AlarmManager service = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, MapDataRecieved.class);
            PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, 30);
            service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(), REPEAT_TIME, pending);


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public class DeviceListReady extends BroadcastReceiver {

        public static final String ACTION_DEVICE_LIST_READY = "com.iwaykids.devicelistready";

        @Override
        public void onReceive(Context context, Intent intent) {

            DeviceListParser parser = new DeviceListParser();
            if (parser.getDevices(intent.getStringExtra(Constants.RESPONSE)) != null) {

                list = parser.getDevices(intent.getStringExtra(Constants.RESPONSE));

                DeviceNamesAdapter adapter = new DeviceNamesAdapter(MapActivity.this, list);

                spDeviceName.setAdapter(adapter);
                spDeviceName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        Devices res = (Devices) adapterView.getItemAtPosition(i);

                        mapDataDialog = new ProgressDialog(MapActivity.this);
                        mapDataDialog.setMessage("Loading...");
                        mapDataDialog.setIndeterminate(false);
                        mapDataDialog.show();

                        pref.edit().putString(Constants.SELECTED_DEVICE_ID,"").commit();
                        pref.edit().putString(Constants.SELECTED_DEVICE_ID,res.getId()).commit();
                        reCallMap();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                deviceListProgressDialog.dismiss();


            } else {

                deviceListProgressDialog.dismiss();
            }

        }
    }

    public void reCallMap(){

        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(MapActivity.this, MapDataFetchService.class);
        intent.putExtra(Constants.URL, "http://dizlabs.in/developments/iwaykidz_admin/android/live_tracking.php?deviceID=");

        PendingIntent pintent = PendingIntent.getService(MapActivity.this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 30 * 1000, pintent);
        startService(intent);

    }


//    public class RepeaterAwaked extends BroadcastReceiver {
//
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            Log.i("called","called");
//            Toast.makeText(MapActivity.this,"called",Toast.LENGTH_LONG).show();
//
//        }
//    }

}
