package com.electroware.applocker;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by user on 1.09.2016.
 */
public class RequestPermission extends AppCompatActivity {

    Button goDA;
    static final int ACTIVATION_REQUEST = 47;
    Button reqDeviceAdmin;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ColorManager colorManager;
    RelativeLayout reqLayout;
    boolean ab;
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.request_permission);
        Intent iin= getIntent();
        colorManager = new ColorManager(this);
        reqLayout = (RelativeLayout) findViewById(R.id.reqLayout);
        Bundle b = iin.getExtras();
        updateTheme();
        preferences=RequestPermission.this.getSharedPreferences("device_admin", RequestPermission.this.MODE_PRIVATE);
        editor = preferences.edit();
        try {
            ab = b.getString("first").equals("true");
        }catch (Exception e){

        }
        goDA = (Button) findViewById(R.id.giveDA);
        reqDeviceAdmin = (Button) findViewById(R.id.reqDeviceAdmin);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (!checkUsageStatsPermission(RequestPermission.this)) {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
            }
        }
        if (checkDeviceAdminPermission(RequestPermission.this)){
            reqDeviceAdmin.setText(getString(R.string.uninstall_protect_is_active));
        }else {
            reqDeviceAdmin.setText(getString(R.string.uninstall_protect_is_not_active));
        }

        reqDeviceAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDeviceAdminPermission(RequestPermission.this)){

                }else {
                    requestDeviceAdmin();
                }
            }
        });
    }
    private void updateTheme(){
        if (colorManager.isLight()){
            reqLayout.setBackgroundColor(getColor(R.color.white));
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        if (checkDeviceAdminPermission(RequestPermission.this)){
            reqDeviceAdmin.setText(getString(R.string.uninstall_protect_is_active));
        }else {
            reqDeviceAdmin.setText(getString(R.string.uninstall_protect_is_not_active));
        }
    }
    public static boolean checkUsageStatsPermission(Context context){
        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0,  System.currentTimeMillis());
        return !queryUsageStats.isEmpty();
    }
    public static boolean checkDeviceAdminPermission(Context context){
        SharedPreferences preferences=context.getSharedPreferences("device_admin", context.MODE_PRIVATE);
        boolean granted=preferences.getBoolean("get_da",false);
        return granted;
    }
    private void requestDeviceAdmin(){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        ComponentName deviceAdminComponentName = new ComponentName(this, DeviceAdmin.class);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You must enable device administration for your privacy and security");
        startActivityForResult(intent, ACTIVATION_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVATION_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    if (ab){
                        Log.i("DeviceAdminSample", "Administration enabled!");
                        Intent i = new Intent(RequestPermission.this,SetLockTypeActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
                else {
                    Log.i("DeviceAdminSample", "Administration enable FAILED!");
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
