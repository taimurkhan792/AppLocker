package com.electroware.applocker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 31.08.2016.
 */
public class UnlockActivity extends AppCompatActivity {

    private String app = null;
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        app = b.getString("app");
        if (ManageLockType.getLockType().contains("pattern"))
        {
            Intent i = new Intent(UnlockActivity.this,EnterPatternLock.class);
            i.putExtra("app",app);
            startActivity(i);
            finish();
        }
        if (ManageLockType.getLockType().contains("tp"))
        {
            Intent i = new Intent(UnlockActivity.this,EnterNormalPIN.class);
            i.putExtra("app",app);
            startActivity(i);
            finish();
        }
        if (ManageLockType.getLockType().contains("pin"))
        {
            Intent i = new Intent(UnlockActivity.this,EnterNormalPIN.class);
            i.putExtra("app",app);
            i.putExtra("pin","true");
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
     super.onBackPressed();

    }



}
