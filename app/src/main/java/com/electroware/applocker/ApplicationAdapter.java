package com.electroware.applocker;

/**
 * Created by user on 30.08.2016.
 */
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ApplicationAdapter extends ArrayAdapter<ApplicationInfo>{
    private List<ApplicationInfo> appsList = null;
    private Context context;
    private PackageManager packageManager;
    List<String> allAppList = null;
    List<String> lockedAppList = null;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public ApplicationAdapter(Context context, int textViewResourceId,
                              List<ApplicationInfo> appsList) {
        super(context, textViewResourceId, appsList);
        this.context = context;
        allAppList = new ArrayList<String>();
        lockedAppList = new ArrayList<String>();
        this.appsList = appsList;
        packageManager = context.getPackageManager();
        preferences=context.getSharedPreferences("chosen_apps", context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    @Override
    public int getCount() {
        return ((null != appsList) ? appsList.size() : 0);
    }

    @Override
    public ApplicationInfo getItem(int position) {

        return ((null != appsList) ? appsList.get(position) : null);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.app_list_item, null);
        }

        final ApplicationInfo data = appsList.get(position);
        if (null != data) {
            TextView appName = (TextView) view.findViewById(R.id.app_name);
            ImageView iconview = (ImageView) view.findViewById(R.id.app_icon);
            final SwitchCompat lockApp = (SwitchCompat) view.findViewById(R.id.lockApp);

            appName.setText(data.loadLabel(packageManager));
            iconview.setImageDrawable(data.loadIcon(packageManager));

            if(preferences.getBoolean(data.packageName,false)){
                lockApp.setChecked(true);
            }
            else{
                lockApp.setChecked(false);
            }



        lockApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lockApp.isChecked()){
                    Log.d("tıklanmış",""+data.packageName);

                    editor.putBoolean(data.packageName,true).apply();
                }
                if (!lockApp.isChecked()){
                    Log.d("silinmiş",""+data.packageName);
                    editor.putBoolean(data.packageName,false).apply();
                }
            }
        });
    }
    return view;
}
}
