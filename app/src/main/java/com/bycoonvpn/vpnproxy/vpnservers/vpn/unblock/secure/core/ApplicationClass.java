/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.core;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.firebase.FirebaseApp;
import com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.R;

public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        setTheme();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels();
        }
    }
    private void setTheme(){
        int checkedItem = ApplicationClass.getIntPreference(this,"Theme");
        if (checkedItem == 1){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else if (checkedItem == 2){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannels() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence name = getString(R.string.channel_name_background);
        NotificationChannel mChannel = new NotificationChannel(OpenVPNService.NOTIFICATION_CHANNEL_BG_ID, name, NotificationManager.IMPORTANCE_MIN);
        mChannel.setDescription(getString(R.string.channel_description_background));
        mChannel.enableLights(false);
        mChannel.setLightColor(Color.DKGRAY);
        mNotificationManager.createNotificationChannel(mChannel);
        name = getString(R.string.channel_name_status);
        mChannel = new NotificationChannel(OpenVPNService.NOTIFICATION_CHANNEL_NEWSTATUS_ID, name, NotificationManager.IMPORTANCE_LOW);
        mChannel.setDescription(getString(R.string.channel_description_status));
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        mNotificationManager.createNotificationChannel(mChannel);
        name = getString(R.string.channel_name_userreq);
        mChannel = new NotificationChannel(OpenVPNService.NOTIFICATION_CHANNEL_USERREQ_ID, name, NotificationManager.IMPORTANCE_HIGH);
        mChannel.setDescription(getString(R.string.channel_description_userreq));
        mChannel.enableVibration(true);
        mChannel.setLightColor(Color.CYAN);
        mNotificationManager.createNotificationChannel(mChannel);
    }
    public static int getIntPreference(Context context, String key){
        SharedPreferences preferences = context.getSharedPreferences("Preferences",MODE_PRIVATE);
        return preferences.getInt(key,0);
    }
    public static void setIntPreference(Context context, String key, int value){
        SharedPreferences.Editor editor = context.getSharedPreferences("Preferences",MODE_PRIVATE).edit();
        editor.putInt(key,value);
        editor.apply();
    }
    public static String getStringPreference(String key, Context context){
        SharedPreferences preferences = context.getSharedPreferences("Preferences",MODE_PRIVATE);
        return preferences.getString(key,"");
    }
    public static void setStringPreference(String key, String value, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences("Preferences",MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }
}
