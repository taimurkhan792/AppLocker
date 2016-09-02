package com.electroware.applocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by user on 1.09.2016.
 */
public class ScreenReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.i("Check", "Screen went OFF");
            stopLockService(context);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.i("Check", "Screen went ON");
            startLockService(context);

        }
    }
    private void startLockService(Context context){
        Intent i = new Intent(context, LockService.class);
        context.startService(i);
    }
    private void stopLockService(Context context){
        Intent i = new Intent(context, LockService.class);
        context.stopService(i);
    }
}
