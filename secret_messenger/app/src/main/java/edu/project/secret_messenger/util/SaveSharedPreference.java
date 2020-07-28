package edu.project.secret_messenger.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SaveSharedPreference {
    static SharedPreferences getSharedPreferences(Context context){ return PreferenceManager.getDefaultSharedPreferences(context);}

    public static void setAuto(Context context, String id, String pw){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.child("user").child(id).child("isLogin").setValue(true);
        SharedPreferences.Editor autoLogin = getSharedPreferences(context).edit();
        autoLogin.putString("inputId",id);
        autoLogin.putString("inputPw",pw);
        autoLogin.commit();
    }
    public static String getId(Context context){ return getSharedPreferences(context).getString("inputId",null);}
    public static String getPw(Context context){ return getSharedPreferences(context).getString("inputPw",null);}
    public static void logOut(Context context, String id){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("user").child(id).child("isLogin");
        ref.setValue(false);

        SharedPreferences.Editor logout = getSharedPreferences(context).edit();
        logout.clear();
        logout.commit();
    }
}
