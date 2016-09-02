package com.electroware.applocker;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by user on 1.09.2016.
 */
public class DeviceAdmin extends DeviceAdminReceiver {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        preferences=context.getSharedPreferences("device_admin", context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean("get_da",true);
        Log.i("Device Admin: ", "Enabled");
    }

    @Override
    public String onDisableRequested(Context context, Intent intent) {
        return "Admin disable requested";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Log.i("Device Admin: ", "Disabled");
        preferences=context.getSharedPreferences("device_admin", context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean("get_da",false);
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
        Log.i("Device Admin: ", "Password changed");
    }

}