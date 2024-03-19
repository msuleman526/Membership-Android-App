package com.appsnipp.e4solutions.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.appsnipp.e4solutions.Models.Login;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;

public class SessionUtil {
    private static SharedPreferences pref;
    public static Boolean sessionChecked = false;
    private static SharedPreferences.Editor editor;

    public static void saveData(Activity activity, JSONObject object)
    {
        pref = activity.getApplicationContext().getSharedPreferences("SessionPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String offic = gson.toJson(object);
        editor.putString("object", offic);
        editor.commit();
    }

    public static JSONObject getData(final Activity activity) {
        pref = activity.getApplicationContext().getSharedPreferences("SessionPref", 0);
        Gson gson = new Gson();
        String office = pref.getString("object","");
        JSONObject data = new JSONObject();
        if(!office.equals(""))
        {
            Type type = new TypeToken<JSONObject>() {}.getType();
            data = gson.fromJson(office,type);
        }
        return data;
    }

    public static String getEmail(final Activity activity) {
        pref = activity.getApplicationContext().getSharedPreferences(Constants.SESSION_PREF, 0);
        return pref.getString(Constants.SESSION_EMAIL,"");
    }

    public static String getName(final Context activity) {
        pref = activity.getSharedPreferences(Constants.SESSION_PREF, 0);
        return pref.getString(Constants.SESSION_NAME,"");
    }

    public static String getToken(final Activity activity) {
        pref = activity.getApplicationContext().getSharedPreferences(Constants.SESSION_PREF, 0);
        return pref.getString(Constants.SESSION_TOKEN,"");
    }

    public static String getUrl(final Activity activity) {
        pref = activity.getApplicationContext().getSharedPreferences(Constants.SESSION_PREF, 0);
        return pref.getString("api_url","");
    }

    public static String getUrl(final Context activity) {
        pref = activity.getApplicationContext().getSharedPreferences(Constants.SESSION_PREF, 0);
        return pref.getString("api_url","");
    }

    public static Login getLoginData(final Activity activity) {
        pref = activity.getApplicationContext().getSharedPreferences(Constants.SESSION_PREF, 0);
        Gson gson = new Gson();
        String login = pref.getString("session_login","");
        Login logiiin = gson.fromJson(login,Login.class);
        return logiiin;
    }

    public static void setSession(Activity activity, Login loginResponse) {
        pref = activity.getApplicationContext().getSharedPreferences(Constants.SESSION_PREF, 0);
        editor = pref.edit();
        editor.putString(Constants.SESSION_NAME, loginResponse.data.tokenAuth.user.firstName + " " + loginResponse.data.tokenAuth.user.lastLogin);
        editor.putString(Constants.SESSION_EMAIL, loginResponse.data.tokenAuth.user.email);
        editor.putString(Constants.SESSION_TOKEN, loginResponse.data.tokenAuth.token);
        editor.putString("api_url", loginResponse.data.tokenAuth.user.organization.offsiteApi);
        Gson gson = new Gson();
        String offic = gson.toJson(loginResponse);
        editor.putString("session_login", offic);
        editor.commit();
    }

    public static void setSignOut(final Activity activity){

        pref = activity.getApplicationContext().getSharedPreferences(Constants.SESSION_PREF, 0);
        editor = pref.edit();
        editor.remove(Constants.SESSION_NAME);
        editor.remove(Constants.SESSION_EMAIL);
        editor.remove(Constants.SESSION_TOKEN);
        editor.remove("api_url");
        editor.remove("session_login");
        editor.commit();


    }

}