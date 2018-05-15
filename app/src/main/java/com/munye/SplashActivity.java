package com.munye;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.munye.user.R;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.HttpRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends ActionBarBaseActivity implements View.OnClickListener, AsyncTaskCompleteListener {

    private Button btnSplashSignIn , btnSplashRegister;
    private Timer checkInternetTimer;
    final Congrats alertDialoge = new Congrats();
    final Blast blast = new Blast();
    String server_url = "http://jimmiejob.com/index.php/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!isNotServerRunning(SplashActivity.this))
            buildDialog(SplashActivity.this).show();
        else {

            Calendar c = Calendar.getInstance();
            final int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

            // Handler which will run after 2 seconds.
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (timeOfDay >= 0 && timeOfDay < 12){

                        Toast toast = new Toast(SplashActivity.this);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);

                        LayoutInflater li = getLayoutInflater();
                        View toastAppear = li.inflate(R.layout.customtoast_layout2, (ViewGroup) findViewById(R.id.CustomToastLayoutRoot));
                        toast.setView(toastAppear);
                        toast.show();

                        //Toast.makeText(MapActivity.this, "Good Morning, Welcome to jimmie jobs", Toast.LENGTH_LONG).show();
                    } else if (timeOfDay >= 12 && timeOfDay < 16){

                        Toast toast = new Toast(SplashActivity.this);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.setDuration(Toast.LENGTH_SHORT);

                        LayoutInflater li = getLayoutInflater();
                        View toastAppear = li.inflate(R.layout.customtoast_layout3, (ViewGroup) findViewById(R.id.CustomToastLayoutRoot));
                        toast.setView(toastAppear);
                        toast.show();

                        //Toast.makeText(MapActivity.this, "Good Afternoon, Welcome to jimmie jobs", Toast.LENGTH_LONG).show();
                    } else if (timeOfDay >= 16 && timeOfDay < 21){

                        Toast toast = new Toast(SplashActivity.this);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.setDuration(Toast.LENGTH_SHORT);

                        LayoutInflater li = getLayoutInflater();
                        View toastAppear = li.inflate(R.layout.customtoast_layout, (ViewGroup) findViewById(R.id.CustomToastLayoutRoot));
                        toast.setView(toastAppear);
                        toast.show();

                        //Toast.makeText(MapActivity.this, "Good Evening, Welcome to jimmie jobs", Toast.LENGTH_LONG).show();
                    } else if (timeOfDay >= 21 && timeOfDay < 24){


                        Toast toast = new Toast(SplashActivity.this);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);

                        LayoutInflater li = getLayoutInflater();
                        View toastAppear = li.inflate(R.layout.customtoast_layout4, (ViewGroup) findViewById(R.id.CustomToastLayoutRoot));
                        toast.setView(toastAppear);
                        toast.show();
                        //Toast.makeText(MapActivity.this, "Good Night, Welcome to jimmie jobs", Toast.LENGTH_LONG).show();
                    }
                }
            }, 900);


        }

        alertDialoge.showDialog(SplashActivity.this, "JimmieJob");

        new CountDownTimer(1500, 1500) {

                public void onTick(long millisUntilFinished) {


                }

            public void onFinish() {
                alertDialoge.dialog.dismiss();
            }
        }.start();
        preferenceHelper.putTimeZone(getTimeZone());
        btnSplashSignIn = (Button)findViewById(R.id.btnSplashSignIn);
        btnSplashRegister = (Button)findViewById(R.id.btnSplashRegister);
        btnSplashSignIn.setOnClickListener(this);
        btnSplashRegister.setOnClickListener(this);

        if (AppStatus.getInstance(this).isOnline()) {

           // Toast.makeText(getApplicationContext(), "Online", Toast.LENGTH_SHORT).show();

        } else {

          //  Toast.makeText(getApplicationContext(), "Offline", Toast.LENGTH_SHORT).show();
        }

    }

       public boolean isNotServerRunning(final Context context) {
        final RequestQueue requestQueue = Volley.newRequestQueue(SplashActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, server_url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        RequestQueue connection = null;
                        if (requestQueue == connection){
                        //buildDialog(context).show();
                        Toast.makeText(SplashActivity.this, "You are now connected to our server", Toast.LENGTH_LONG).show();
                        requestQueue.start();
                    }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                if (requestQueue != null){

                    buildDialog(context).show();

                }else {

                    volleyError.printStackTrace();
                    requestQueue.stop();

                    Toast.makeText(SplashActivity.this, "You are now connected to our server", Toast.LENGTH_LONG).show();
                }

            }
        });

        requestQueue.add(stringRequest);

        return true;
    }

    public android.support.v7.app.AlertDialog.Builder buildDialog(Context cxt) {


        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(cxt);
        builder.setTitle("Oops! Sever response failed...!");
        builder.setMessage("Please wait for the server to respond or try again later...!");

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        return builder;
    }


    public static class AppStatus {

        private static AppStatus instance = new AppStatus();
         static Context context;
        ConnectivityManager connectivityManager;
        NetworkInfo wifiInfo, mobileInfo;
        boolean connected = false;

        public static AppStatus getInstance(Context ctx) {
            context = ctx.getApplicationContext();
            return instance;
        }

        public boolean isOnline() {
            try {
                connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                connected = networkInfo != null && networkInfo.isAvailable() &&
                        networkInfo.isConnected();
                return connected;


            } catch (Exception e) {
                System.out.println("CheckConnectivity Exception: " + e.getMessage());

            }
            return connected;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopInternetCheck();
        checkInternetStatus();
        if(!AndyUtils.isNetworkAvailable(this))
            startInternetCheck();

    }

    private void checkInternetStatus(){
        if(AndyUtils.isNetworkAvailable(this) && preferenceHelper.getId() == null){
            stopInternetCheck();
            getSettings();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnSplashRegister.setVisibility(View.VISIBLE);
                    btnSplashSignIn.setVisibility(View.VISIBLE);
                }
            });
            closeInternetDialog();
        }
        else if(AndyUtils.isNetworkAvailable(this) && preferenceHelper.getId() != null){
            getSettings();
        }
        else {
            openInternetDialog(this);
        }
    }


    private void startInternetCheck(){

        checkInternetTimer = new Timer();
        TimerTask taskCheckInterner = new TimerTask() {
            @Override
            public void run() {
                checkInternetStatus();
            }
        };
        checkInternetTimer.scheduleAtFixedRate(taskCheckInterner, 1000 , 5000);
    }

    private void stopInternetCheck(){
        if(checkInternetTimer != null){
            checkInternetTimer.cancel();
            checkInternetTimer.purge();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopInternetCheck();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnSplashSignIn:
                startActivity(new Intent(SplashActivity.this , SignInActivity.class));
                break;

            case R.id.btnSplashRegister:
                startActivity(new Intent(SplashActivity.this , RegisterActivity.class));
                break;

            default:
                AndyUtils.generateLog("No action");
                break;
        }
    }

    private String getTimeZone()
    {
        return java.util.TimeZone.getDefault().getID();
    }



    private void getSettings(){
        HashMap<String , String> map = new HashMap<>();
        map.put(Const.URL , Const.ServiceType.GET_SETTINGS);

        new HttpRequester(this , map , Const.ServiceCode.GET_SETTINGS , Const.httpRequestType.POST , this);
    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        if(serviceCode == Const.ServiceCode.GET_SETTINGS && dataParser.isSuccess(response)){

            dataParser.parseSettings(response);
            if(preferenceHelper.getId() != null){
                stopInternetCheck();

                try {

//                        blast.showDialog(SplashActivity.this, "JimmieJob");

                        // Toast.makeText(getApplicationContext(), "Welcome Back", Toast.LENGTH_SHORT).show();
                        new CountDownTimer(1800, 1800) {

                            public void onTick(long millisUntilFinished) {


                            }

                            public void onFinish() {
                                Intent i = new Intent(getApplicationContext(),LandingActivity.class);
                                i.putExtra("show_opt","show");
                                startActivity(i);
                                finish();
                            }
                        }.start();

                        //Toast.makeText(MapActivity.this, "Good Morning, Welcome to jimmie jobs", Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                }


            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopInternetCheck();
    }
}
