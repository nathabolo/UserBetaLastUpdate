package com.munye.utils;

import android.util.Log;

public class AppLog {

    private static final boolean isDebug = true;

    public static  void Log(String TAG,String message){
        if (isDebug){
            Log.i(TAG,message);
        }
    }
}
