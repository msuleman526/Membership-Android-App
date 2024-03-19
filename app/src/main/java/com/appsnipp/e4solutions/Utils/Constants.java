package com.appsnipp.e4solutions.Utils;

public interface Constants {

    public static final String TAG = "EMPOWERENERGYSOLUTIONS";
    public static final boolean IS_LOGG_ENABLE = true;
    public static int UPDATE_INTERVAL = 5000;
    public static int FATEST_INTERVAL = 3000;
    public static int DISPLACEMENT = 10;

    public static final int MY_PERMISSION_REQUEST_CODE = 7192;
    public static final int PLAY_SERVICE_RESULATION_REQUEST = 300193;
    public static final int PRIVATE_MODE = 0;

    public static final String SESSION_ENDPOINT = "SESSION_ENDPOINT";
    public static final String SESSION_PREF = "SessionPref";
    public static final String SESSION_EMAIL = "SESSION_EMAIL";
    public static final String SESSION_NAME = "SESSION_NAME";
    public static final String SESSION_MAP_TYPE = "SESSION_MAP_TYPE";
    public static final String SESSION_FIRST_LOGIN = "SESSION_FIRST_LOGIN";
    public static final String SESSION_LAST_LAT = "SESSION_LAST_LAT";
    public static final String SESSION_LAST_LONG = "SESSION_LAST_LONG";
    public static final String SESSION_LAST_ZOOM = "SESSION_LAST_ZOOM";
    public static final String SESSION_TOKEN = "SESSION_TOKEN";
    public static final String SESSION_TYPE = "SESSION_TYPE";
    public static final String SESSION_OFFICE_ID = "SESSION_OFFICE_ID";
    public static final String SESSION_OFFICES = "SESSION_OFFICES";
    public static final String SESSION_ID = "SESSION_ID";

    public static String MESSAGE_API_LINK = "http://18.138.63.247:11102/graphql";


    public static  boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}
