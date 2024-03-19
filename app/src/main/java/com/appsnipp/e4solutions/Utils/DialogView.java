package com.appsnipp.e4solutions.Utils;

import android.app.Activity;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class DialogView {

    public static void showAleryDialog(int type, Activity activity, String title, String message){
        new SweetAlertDialog(activity,type)
                .setTitleText(title)
                .setContentText(message)
                .show();
    }
}
