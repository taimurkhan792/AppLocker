package com.electroware.applocker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 1.09.2016.
 */
public class SettingsActivity extends AppCompatActivity {

    ListView listemiz;
    Context context;
    TextView vName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        context = SettingsActivity.this;
        listemiz=(ListView) findViewById(R.id.settings_list);
        vName = (TextView) findViewById(R.id.vName);
        String[] settings =
                {context.getString(R.string.reset_password),context.getString(R.string.reset_locked_apps)};

        vName.setText(getString(R.string.app_name)+" v"+GetVersion.getVersion(context));

        ArrayAdapter<String> veriAdaptoru=new ArrayAdapter<String>
                (this, R.layout.settings_list, R.id.settingsTV, settings);
        listemiz.setAdapter(veriAdaptoru);

        listemiz.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                if (position==0){
                    Intent i = new Intent(SettingsActivity.this,SetLockTypeActivity.class);
                    startActivity(i);
                }
                if (position==1){
                    ManageLockedApps.resetLockedApps(context);
                }
                if (position==2){
                    ManageLockedApps.resetLockedApps(context);
                }
            }
        });

    }

}
