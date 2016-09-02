package com.electroware.applocker;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Emre2 on 23.6.2016.
 */
public class ManageLockType {
    public static void setLockType(String lockType,Context context){
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("p", Context.MODE_PRIVATE);
            fileOutputStream.write(lockType.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {

        }
    }
    public static void resetLockType(Context context){
        try {
            String a = "";
            FileOutputStream fileOutputStream = context.openFileOutput("p", Context.MODE_PRIVATE);
            fileOutputStream.write(a.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {

        }
    }
    public static String getLockType(){
        File file = new File("/data/data/com.electroware.applocker/files/p");
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
            Log.d("okuma hatası","no 1");
        }

        return text.toString();
    }

}
