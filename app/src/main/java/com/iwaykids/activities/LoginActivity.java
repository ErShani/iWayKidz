package com.iwaykids.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iwaykids.R;
import com.iwaykids.models.Login;
import com.iwaykids.services.ConnectionDetector;
import com.iwaykids.services.LoginService;
import com.iwaykids.utils.Constants;
import com.iwaykids.utils.LoginParser;

public class LoginActivity extends AppCompatActivity {

    TextView tvLoginTvbtn;
    EditText etUsername, etPassword;
    LinearLayout btnSubmit;
    Typeface tfOpenRegular,tfOpenLight;
    ProgressDialog progressDialog;
    CheckBox cbRemember;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getSharedPreferences(Constants.APP_PREF_NAME,MODE_PRIVATE);

        IntentFilter filter = new IntentFilter(LoginDone.ACTION_LOGIN_DONE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(new LoginDone(),filter);

        pref = getSharedPreferences(Constants.APP_PREF_NAME, MODE_PRIVATE);

        if(pref.getBoolean(Constants.USER_LOGIN_STATUS, false)==true) {

            Intent mainActivity = new Intent(this, MapActivity.class);

            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainActivity);

        }else {


                tfOpenRegular = Typeface.createFromAsset(getAssets(), "normal.ttf");
                tfOpenLight = Typeface.createFromAsset(getAssets(), "open_sans_light.ttf");


//            tvPlease = (TextView) findViewById(R.id.tv_please);
//            tvPlease.setTypeface(tfOpenRegular);

                cbRemember = (CheckBox) findViewById(R.id.cb_remember);

                etUsername = (EditText) findViewById(R.id.et_username);
                etUsername.setTypeface(tfOpenRegular);

                etPassword = (EditText) findViewById(R.id.et_password);
                etPassword.setTypeface(tfOpenRegular);

                if(pref.getBoolean(Constants.IS_REMEMBERED, false)){

                    etUsername.setText(pref.getString(Constants.EMAIL_ID,""));
                    etPassword.setText(pref.getString(Constants.PASSWORD,""));
                    cbRemember.setChecked(true);

                }else {

                    cbRemember.setChecked(false);
                    etPassword.setText("");
                    etUsername.setText("");
                }

                tvLoginTvbtn = (TextView) findViewById(R.id.tvLoginBarText);
                tvLoginTvbtn.setTypeface(tfOpenRegular);

                btnSubmit = (LinearLayout) findViewById(R.id.btn_login_submit);
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (isValid()) {

                            // pref.edit().putBoolean(Constants.USER_LOGIN_STATUS, true).commit();

                            if(ConnectionDetector.isNetworkAvailable(LoginActivity.this)) {

                                Log.i("Connection true", "true");
                                progressDialog = new ProgressDialog(LoginActivity.this);
                                progressDialog.setMessage("Please Wait..");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                Log.i("login url", Constants.BASE_URL + "/login.php?email=" + etUsername.getText().toString().trim() + "&password=" + etPassword.getText().toString().trim());

                                Intent mainActivity = new Intent(LoginActivity.this, LoginService.class);
                                mainActivity.addCategory(Intent.CATEGORY_DEFAULT);
                                mainActivity.putExtra(Constants.LOGIN_URL, Constants.BASE_URL + "/login.php?email=" + etUsername.getText().toString().trim() + "&password=" + etPassword.getText().toString().trim());
                                startService(mainActivity);
                            }else {

                                final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "NO INTERNET CONNECTION", Snackbar.LENGTH_LONG);
                                snackbar.setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        snackbar.dismiss();
                                    }
                                })
                                        .setActionTextColor(Color.RED)
                                        .show();

                            }

                        } else {

                            final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "ENTER ALL DETAILS", Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    snackbar.dismiss();
                                }
                            })
                                    .setActionTextColor(Color.RED)
                                    .show();
                        }
                    }
                });

            cbRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {

                        if (isValid()) {

                            pref.edit().putBoolean(Constants.IS_REMEMBERED, true).commit();
                            pref.edit().putString(Constants.EMAIL_ID, etUsername.getText().toString()).commit();
                            pref.edit().putString(Constants.PASSWORD, etPassword.getText().toString()).commit();
                        }
                    } else {

                        pref.edit().putBoolean(Constants.IS_REMEMBERED, false).commit();
                        pref.edit().putString(Constants.EMAIL_ID, "").commit();
                        pref.edit().putString(Constants.PASSWORD, "").commit();
                    }
                }
            });



        }

    }

   public boolean isValid(){

       if(etUsername.length()<1){

           return false;
       }
       else if(etPassword.length()<1){

           return false;
       }

       return true;
   }


    public class LoginDone extends BroadcastReceiver {

        public static final String ACTION_LOGIN_DONE = "com.iwaykids.logindone";

        @Override
        public void onReceive(Context context, Intent intent) {

            LoginParser parser = new LoginParser();
            if(parser.getUserID(intent.getStringExtra(Constants.RESPONSE)).equals("False")){

                progressDialog.dismiss();

                final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "INVALID USERNAME OR PASSWORD", Snackbar.LENGTH_LONG);
                snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        snackbar.dismiss();
                    }
                })
                        .setActionTextColor(Color.RED)
                        .show();
            }else {

                pref.edit().putBoolean(Constants.USER_LOGIN_STATUS, true).commit();
                pref.edit().putString(Constants.USERID, parser.getUserID(intent.getStringExtra(Constants.RESPONSE))).commit();

                finish();
                Intent mainActivity = new Intent(LoginActivity.this, MapActivity.class);

                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mainActivity.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(mainActivity);

            }

        }
    }


}
