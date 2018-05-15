package com.munye;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.munye.user.R;

/**
 * Created by jimmiejobscreative on 2018/04/20.
 */


public class Done {
    public Dialog dialog;

    public void showDialog(Activity activity, String msg) {

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.done);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();

    }


}