package com.munye.utils;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import com.munye.user.R;

import java.io.IOException;
import java.util.Calendar;

import static com.munye.utils.Const.EXCEPTION;
import static com.munye.utils.Const.TAG;

/**
 * Created by Akash on 1/16/2017.
 */

public class AndyUtils {

    private static Dialog dialog;


    public static void showToast(Context context , String message){
        Toast.makeText(context , message , Toast.LENGTH_LONG).show();
    }

    public static String getRealPathFromURI(Uri contentURI, Context context){

        String result=null;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null,
                null, null);

        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            try {
                int idx = cursor
                        .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
            } catch (Exception e) {
                AppLog.Log("tag","Exception"+e);
            }
            cursor.close();
        }
        return result;
    }
    public static int setRotaionValue(String filePath){

        ExifInterface exif ;
        int rotationAngle = 0;
        try {
            AppLog.Log("tag","File path - "+filePath);
            exif = new ExifInterface(filePath);
            String orientString = exif
                    .getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer
                    .parseInt(orientString)
                    : ExifInterface.ORIENTATION_NORMAL;

            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationAngle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationAngle = 180;
                    break;
                case  ExifInterface.ORIENTATION_ROTATE_270:
                    rotationAngle = 270;
                    break;
                default:
                    rotationAngle = 0;
                    break;
            }
        } catch (IOException e) {
            AppLog.Log(Const.TAG, EXCEPTION+e);
        }

        return rotationAngle;
    }


    public static String getPathForBitmap(Bitmap photoBitmap, Context context, int rotationAngle){

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle,
                (float) photoBitmap.getWidth() / 2,
                (float) photoBitmap.getHeight() / 2);
        Bitmap photoBitmapData = Bitmap.createBitmap(photoBitmap, 0, 0,
                photoBitmap.getWidth(),
                photoBitmap.getHeight(), matrix, true);

        return MediaStore.Images.Media.insertImage(
                context.getContentResolver(), photoBitmapData, Calendar
                        .getInstance().getTimeInMillis()
                        + ".jpg", null);

    }

    public static void generateLog(String logMessage){
        Log.d("tag", logMessage);
    }



    public static void showErrorToast(int id , Context context){

        String message;
        String errorCode = Const.ERROR_CODE_PRIFIX + id;

        message = context.getResources().getString(context.getResources().getIdentifier(errorCode, "string", context.getPackageName()));
        Toast.makeText(context , message , Toast.LENGTH_LONG).show();
    }


    public static String getColorCode(int type , Context context){
        String color;
        String colorCode = Const.COLOR_CODE_PRIFIX + type;
        color = context.getResources().getString(context.getResources().getIdentifier(colorCode , "color" , context.getPackageName()));
        return color;
    }


    public static String getBackgroundColor(int type , Context context){
        String color;
        String colorCode = Const.BACKGROUND_COLOR_CODE_PRIFIX + type;
        color = context.getResources().getString(context.getResources().getIdentifier(colorCode , "color" , context.getPackageName()));
        return color;
    }

    public static String getSymbolFromHex(String hexValue){
        return String.valueOf(Html.fromHtml(hexValue));
    }

    public static void showCustomProgressDialog(Context context , boolean isCancelable){

        if(dialog != null && dialog.isShowing())
            return;

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_dialog_view);
        ImageView imgPogrssDialog = (ImageView)dialog.findViewById(R.id.imgPogrssDialog);
        imgPogrssDialog.setAnimation(AnimationUtils.loadAnimation(context , R.anim.rotation_animation));
        dialog.setCancelable(isCancelable);
        dialog.show();

    }


    public static void removeCustomProgressDialog(){
        try{
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
            dialog = null;
        }
    }catch (IllegalArgumentException ie) {
            AppLog.Log(TAG,EXCEPTION+ie);
        }
        }


    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = actNetworkInfo != null && actNetworkInfo.isConnectedOrConnecting();
        return isConnected;
    }

    public static boolean isGpsEnable(Context context){
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }


    public static boolean hasAnyPrifix(String number, String... prefixes){
        if (number == null) {
            return false;
        }
        for (String prefix : prefixes) {
            if (number.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
