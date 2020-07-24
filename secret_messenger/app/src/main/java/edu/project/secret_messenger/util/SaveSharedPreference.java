package edu.project.secret_messenger.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {
    static SharedPreferences getSharedPreferences(Context context){ return PreferenceManager.getDefaultSharedPreferences(context);}

    public static void setAuto(Context context, String id, String pw){
        SharedPreferences.Editor autoLogin = getSharedPreferences(context).edit();
        autoLogin.putString("inputId",id);
        autoLogin.putString("inputPw",pw);
        autoLogin.commit();
    }
    public static String getId(Context context){ return getSharedPreferences(context).getString("inputId",null);}
    public static String getPw(Context context){ return getSharedPreferences(context).getString("inputPw",null);}
    public static void logOut(Context context){
        SharedPreferences.Editor logout = getSharedPreferences(context).edit();
        logout.clear();
        logout.commit();
    }
}
