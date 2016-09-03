package com.electroware.applocker;

/**
 * Created by user on 30.08.2016.
 */
import java.util.ArrayList;
import java.util.List;
import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private ApplicationAdapter listadaptor = null;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    TextView goSettings;
    ListView listApps;
    Context context;
    Button go_AndroBooster,goPerms,lockAll,unlockAll;
    InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        packageManager = getPackageManager();
        listApps = (ListView) findViewById(R.id.listApps);
        go_AndroBooster = (Button) findViewById(R.id.go_booster);
        goPerms = (Button) findViewById(R.id.goPerms);
        lockAll = (Button) findViewById(R.id.lock_all);
        unlockAll = (Button) findViewById(R.id.unlock_all);
        goSettings = (TextView) findViewById(R.id.lockSettings);

        listApps.setItemsCanFocus(true);

        if(!isMyServiceRunning(LockService.class)){
            startLockService();
        }

        if (!isMyServiceRunning(ScreenService.class)){
            startService(new Intent(context,ScreenService.class));
        }


        mInterstitialAd = new InterstitialAd(this);

        mInterstitialAd.setAdUnitId("ca-app-pub-4481645276167910/8991689789");

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });

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


        new LoadApplications().execute();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
        }

        startReqUsageStat();

    }
    private void startReqUsageStat(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (!checkUsageStatsPermission()){
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
                finish();
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
    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
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
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            listadaptor = new ApplicationAdapter(MainActivity.this,
                    R.layout.app_list_item, applist);

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
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, null,
                    "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
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
        finish();
    }
    private void startLockService() {
        Intent i = new Intent(MainActivity.this, LockService.class);
        startService(i);
    }
    private void startScreenService() {
        Intent i = new Intent(MainActivity.this, ScreenService.class);
        startService(i);
    }
    private void stopLockService() {
        Intent i = new Intent(MainActivity.this, LockService.class);
        stopService(i);
    }
    private void restartService() {
        Intent i = new Intent(MainActivity.this, LockService.class);
        stopService(i);
        startService(i);
    }
    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (!info.packageName.equals("com.electroware.applocker")) {
                    if (!info.packageName.contains("launcher3")) {
                        if (!info.packageName.contains("launcher")) {
                            if (!info.packageName.contains("trebuchet")) {
                                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                                    applist.add(info);
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