package com.electroware.applocker;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by user on 1.09.2016.
 */
public class ScreenService extends Service {
    ScreenReciever screenReciever;

    @Override
    public void onCreate(){
        super.onCreate();
        screenReciever = new ScreenReciever();
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReciever, screenStateFilter);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(screenReciever);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent ıntent) {
        return null;
    }
}
