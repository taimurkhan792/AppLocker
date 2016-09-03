package com.electroware.applocker;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * Created by user on 31.08.2016.
 */
public class LockService extends Service {

    SaveState saveState;
    Context context;
    private String locked_app = null;
    private String current_app = null;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private int mInterval = 1755;
    private Handler mHandler;

    @Override
    public void onCreate(){
        super.onCreate();
        mHandler = new Handler();
        startRepeatingTask();
    }
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                try {
                    context = LockService.this;
                    preferences=context.getSharedPreferences("chosen_apps", context.MODE_PRIVATE);
                    editor = preferences.edit();
                    saveState = new SaveState(context);
                    current_app = getRecentApps(context);
                    boolean lock_apps = saveState.getState();
                    Log.d("current apps", current_app);
                    if (lock_apps){
                        if (preferences.getBoolean(current_app,false)){
                            locked_app = getRecentApps(context);
                            Log.d("locked app",locked_app);
                            startUnlockActivity(getRecentApps(context));
                        }
                    }
                    if (!lock_apps){
                        if (!current_app.equals(locked_app)){
                            Log.d("11111","hm");
                            saveState.saveState("true");
                        }
                        if (current_app.equals(locked_app)){
                            Log.d("running",locked_app);
                        }
                    }
                }catch (Exception e){

                }

            } finally {

                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };
    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }
    public String getRecentApps(Context context){
        String topPackageName = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*10, time);
            if(stats != null) {
                SortedMap<Long,UsageStats> mySortedMap = new TreeMap<Long,UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
                }
                if(!mySortedMap.isEmpty()) {
                    topPackageName =  mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        }else {
            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            topPackageName = componentInfo.getPackageName();
        }
        return topPackageName;
    }
    public void startUnlockActivity(String packagename){
        if (ManageLockType.getLockType().equals("tp")){
            Intent i = new Intent(LockService.this,EnterNormalPIN.class);
            i.putExtra("app",packagename);
            i.putExtra("try","true");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        if (ManageLockType.getLockType().equals("pin")){
            Intent i = new Intent(LockService.this,EnterNormalPIN.class);
            i.putExtra("app",packagename);
            i.putExtra("pin","true");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        if (ManageLockType.getLockType().equals("pattern")){
            Intent i = new Intent(LockService.this,EnterPatternLock.class);
            i.putExtra("app",packagename);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }





}
