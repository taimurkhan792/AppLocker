package com.electroware.applocker;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 7.09.2016.
 */
public class SaveLogs {
    Context context;
    SaveLogs(Context context1){
        context = context1;
    }
    public String getLogs(){
        File file = new File("/data/data/com.electroware.applocker/files/logs");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        }
        catch (IOException e) {

        }
        return text.toString();
    }
    public void saveLogs(String appName,boolean success){
        try {
            String log_success = null;
            if (success) {
                log_success = context.getString(R.string.entered_successfully);
            }
            if (!success) {
                log_success = context.getString(R.string.enter_failed);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());
            String name = context.getString(R.string.date)+currentDateandTime+" "+log_success+" "+context.getString(R.string.entered_app)+appName+"\n";
            FileOutputStream fOut = context.openFileOutput("logs",  context.MODE_PRIVATE | context.MODE_APPEND);
            fOut.write(name.getBytes());
            fOut.flush();
            fOut.close();
        }
        catch (Exception e) {
            Log.e("Hata kodu3", "File write failed: " + e.toString());
        }
    }
    public void deleteLogs(){
        try {
            FileOutputStream fOut = context.openFileOutput("logs",  context.MODE_PRIVATE);
            String name = "";
            fOut.write(name.getBytes());
            fOut.flush();
            fOut.close();
        }
        catch (Exception e) {
            Log.e("Hata kodu3", "File write failed: " + e.toString());
        }
    }
}
