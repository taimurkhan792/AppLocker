package com.electroware.applocker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 31.08.2016.
 */
public class Splash extends AppCompatActivity {

    private Button start_now;
    private SaveState saveState;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;
    ImageView splashIcon;
    Animation slideDown;
    TextView slogan;
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.splash);
        context = Splash.this;
        saveState = new SaveState(this);
        start_now = (Button) findViewById(R.id.start_now);
        preferences=context.getSharedPreferences("chosen_apps", context.MODE_PRIVATE);
        editor = preferences.edit();
        splashIcon = (ImageView) findViewById(R.id.appIconSplash);
        slogan = (TextView) findViewById(R.id.slogan);

        ManageLockedApps.resetLockedApps(context);

        slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);

        splashIcon.setAnimation(slideDown);

        slogan.setAnimation(slideDown);

        ManageLockType.setLockType("",Splash.this);
        saveState.saveState("true");

        start_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Splash.this,SetLockTypeActivity.class);
                i.putExtra("first","true");
                startActivity(i);
                finish();
            }
        });
    }
}
