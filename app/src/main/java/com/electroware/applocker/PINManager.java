package com.electroware.applocker;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.scottyab.aescrypt.AESCrypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by user on 31.08.2016.
 */
public class PINManager {
    public static String AB = "qwEwfbfs";
    public static void setPIN(String PIN,Context context){
        try {
            String encryptedMsg = AESCrypt.encrypt(AB, PIN);
            FileOutputStream fileOutputStream = context.openFileOutput("pin", Context.MODE_PRIVATE);
            fileOutputStream.write(encryptedMsg.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(context, context.getString(R.string.pin_setted),Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }
    }
    public static String getPIN(){
        File file = new File("/data/data/com.electroware.applocker/files/pin");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        }
        catch (Exception e) {
            Log.d("okuma hatası","no 1");
        }
        String messageAfterDecrypt = null;
        try {
            messageAfterDecrypt = AESCrypt.decrypt(AB, text.toString());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return messageAfterDecrypt;
    }
}
