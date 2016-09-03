package com.electroware.applocker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by user on 31.08.2016.
 */
public class EnterActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);

        final String PREFS_NAME = "enter";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("first_time", true)) {
            settings.edit().putBoolean("first_time", false).commit();
            Intent i = new Intent(EnterActivity.this,Splash.class);
            startActivity(i);
            finish();
        }else {
            if (ManageLockType.getLockType().equals("")){
                Intent i = new Intent(EnterActivity.this,SetLockTypeActivity.class);
                startActivity(i);
                finish();
            }
            if (ManageLockType.getLockType().equals("pin")){
                Intent i = new Intent(EnterActivity.this,EnterNormalPIN.class);
                i.putExtra("main","true");
                startActivity(i);
                finish();
            }
            if (ManageLockType.getLockType().equals("pattern")){
                Intent i = new Intent(EnterActivity.this,EnterPatternLock.class);
                i.putExtra("main","true");
                startActivity(i);
                finish();
            }
        }
    }
}
