package com.appsnipp.e4solutions.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ImageView;

import com.appsnipp.e4solutions.R;
import com.appsnipp.e4solutions.Steps.MainActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Utils {

    public static String toTitleCase(String givenString) {
        givenString = givenString.replace("2", "");
        givenString = givenString.replace("1", "");
        givenString = givenString.replace(".", "");
        givenString = givenString.replace(",", "");
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public static String capitalizeText(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return the original string if it's null or empty
        }

        String firstLetter = input.substring(0, 1).toUpperCase();
        String restOfString = input.substring(1).toLowerCase();

        return firstLetter + restOfString;
    }

    public static boolean isInternetConnected(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    public static String getAddress (String address1, String address2, String address3, String address4, String postCode){
        String address = "";
        if(address1 != null && !address1.equals("")){
            address += ""+address1;
        }
        if(address2 != null && !address2.equals("")){
            address += ", "+address2;
        }
        if(address3 != null && !address3.equals("")){
            address += ", "+address3;
        }
        if(address4 != null && !address4.equals("")){
            address += ", "+address4;
        }
        if(postCode != null && !postCode.equals("")){
            address += ", "+postCode;
        }
        return address;
    }

    public static String firstLetterCapital(String str){
        str = str.replace("2", "");
        str = str.replace("1", "");
        str = str.replace(".", "");
        str = str.replace(",", "");
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void checkDataOnFirstPage(Activity activity, Context context){
        JSONObject jsonObject = SessionUtil.getData(activity);
        if(jsonObject == null){
            Log.d("DAATA", "Data is null");
            context.startActivity(new Intent(activity, MainActivity.class));
            activity.finishAffinity();
        }else{
            try {
                if (jsonObject.has("title") || jsonObject.has("mobilephone")) {
                    new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                            .setContentText("Would you like to clear the current record filled in forms or not?")
                            .setConfirmText("Clear!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    SessionUtil.saveData(activity, null);
                                    context.startActivity(new Intent(activity, MainActivity.class));
                                    activity.finishAffinity();
                                }
                            })
                            .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    context.startActivity(new Intent(activity, MainActivity.class));
                                    activity.finishAffinity();
                                }
                            })
                            .show();
                }else{
                    context.startActivity(new Intent(activity, MainActivity.class));
                    activity.finishAffinity();
                }
            }catch (Exception e){
                e.printStackTrace();
                context.startActivity(new Intent(activity, MainActivity.class));
                activity.finishAffinity();
            }
        }
    }

    public static void getBitmapFromURl(String urll, ImageView imageView){
        Picasso.get().load(urll).into(imageView);
    }
}
