package com.electroware.applocker;

/**
 * Created by user on 30.08.2016.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.Manifest;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.google.firebase.analytics.FirebaseAnalytics;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private ApplicationAdapter listadaptor = null;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    Button goSettings;
    ListView listApps;
    Context context;
    Button go_AndroBooster,goPerms,lockAll,unlockAll,goLogs;

    MaterialProgressBar loadingBar;
    ColorManager colorManager;
    RelativeLayout mainLayout;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        colorManager = new ColorManager(this);
        setSupportActionBar(toolbar);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        packageManager = getPackageManager();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        listApps = (ListView) findViewById(R.id.listApps);
        go_AndroBooster = (Button) findViewById(R.id.go_booster);
        goPerms = (Button) findViewById(R.id.goPerms);
        lockAll = (Button) findViewById(R.id.lock_all);
        unlockAll = (Button) findViewById(R.id.unlock_all);
        loadingBar = (MaterialProgressBar) findViewById(R.id.loadingBar);
        goLogs = (Button) findViewById(R.id.goLogs);
        goSettings = (Button) findViewById(R.id.lockSettings);
        listApps.setItemsCanFocus(true);

        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        startLockService();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startReqUsageStat();
            }
        }, 3000);


        lockAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManageLockedApps.lockAllApps(context);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2000);
            }
        });
        unlockAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManageLockedApps.resetLockedApps(context);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2000);
            }
        });
        goPerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, RequestPermission.class);
                startActivity(i);
            }
        });
        goLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LogsActivity.class);
                startActivity(i);
            }
        });
        go_AndroBooster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startApplication("com.emre.androbooster");
            }
        });
        goSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                finish();
            }
        });
        applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        listadaptor = new ApplicationAdapter(MainActivity.this,
                R.layout.app_list_item, applist);

        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                new LoadApplications().execute();
            }
        }, 1500);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                }
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                }
            }
        }).start();


    }
    private void startLockService() {
        if (!isMyServiceRunning(LockService.class)){
            context.startService(new Intent(context, LockService.class));
        }
    }
    private void stopLockService() {
        context.stopService(new Intent(context, LockService.class));
    }
    private void startReqUsageStat(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (!checkUsageStatsPermission()){
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
                Toast.makeText(context,getString(R.string.please_give_usage_Stats),Toast.LENGTH_LONG).show();
            }
        }
    } public boolean checkUsageStatsPermission(){
        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0,  System.currentTimeMillis());
        return !queryUsageStats.isEmpty();
    }
    public void startApplication(String packageName)
    {
        try
        {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");

            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent, 0);

            for(ResolveInfo info : resolveInfoList)
                if(info.activityInfo.packageName.equalsIgnoreCase(packageName))
                {
                    launchComponent(info.activityInfo.packageName, info.activityInfo.name);
                    return;
                }
            showInMarket(packageName);
        }
        catch (Exception e)
        {
            showInMarket(packageName);
        }
    }
    public String getPattern() {
        File file = new File("/data/data/com.electroware.applocker/files/pattern");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        } catch (IOException e) {
            Log.d("okuma hatası", "no 1");
        }
        return text.toString();
    }
    private void launchComponent(String packageName, String name)
    {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(new ComponentName(packageName, name));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }
    private void showInMarket(String packageName)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final List<ApplicationInfo> appsList = context.getPackageManager().getInstalledApplications(0);
        final ApplicationInfo data = appsList.get(i);
        final SwitchCompat lockApp = (SwitchCompat) view.findViewById(R.id.lockApp);
        lockApp.performClick();
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            listadaptor = new ApplicationAdapter(MainActivity.this,
                    R.layout.app_list_item, applist);
            if(!isMyServiceRunning(LockService.class)){
                startLockService();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            listApps.setAdapter(listadaptor);
            listApps.setOnItemClickListener(MainActivity.this);
            loadingBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            loadingBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
    String[] alphabet = "abcdefghijklmnopqrstuvwxyz".split("");

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        startLockService();
    }
    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {

                if (!info.packageName.equals("com.google.android.googlequicksearchbox")) {
                    if (!info.packageName.equals("com.electroware.applocker")) {
                        if (!info.packageName.contains("launcher3")) {
                            if (!info.packageName.contains("launcher")) {//com.google.android.googlequicksearchbox
                                if (!info.packageName.contains("trebuchet")) {
                                    if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                                        applist.add(info);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
    }
}
