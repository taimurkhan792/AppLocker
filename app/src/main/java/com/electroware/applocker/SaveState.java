package com.electroware.applocker;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by user on 31.08.2016.
 */
public class SaveState {
    Context context;
    SaveState(Context context1){
        context = context1;
    }
    public boolean getState(){
        File file = new File("/data/data/com.electroware.applocker/files/state");
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
        return text.toString().contains("true");
    }
    public void saveState(String name){
        try {
            FileOutputStream fOut = context.openFileOutput("state",  context.MODE_PRIVATE);
            fOut.write(name.getBytes());
            fOut.flush();
            fOut.close();
        }
        catch (Exception e) {
            Log.e("Hata kodu3", "File write failed: " + e.toString());
        }
    }
}
