package com.electroware.applocker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

/**
 * Created by user on 31.08.2016.
 */
public class SetLockTypeActivity extends AppCompatActivity {

    SwitchCompat setPattern,setNormalPIN;
    Context context;
    Button start;
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.set_lock_type);
        context = SetLockTypeActivity.this;
        setPattern = (SwitchCompat) findViewById(R.id.setPattern);
        setNormalPIN = (SwitchCompat) findViewById(R.id.setNormalPINSwitch);
        start = (Button) findViewById(R.id.strt);

        ManageLockType.setLockType("",context);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setPattern.isChecked() && !setNormalPIN.isChecked()){
                    startPattern();
                }
                if (!setPattern.isChecked()&& setNormalPIN.isChecked()){
                    startSetNormalPIN();
                }
                if (!setPattern.isChecked() && !setNormalPIN.isChecked()){
                    Toast.makeText(context,getString(R.string.select_lock_type),Toast.LENGTH_SHORT).show();
                }
            }
        });
        setNormalPIN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    ManageLockType.setLockType("pin",context);
                    setPattern.setChecked(false);
                }
                if (!b){
                    ManageLockType.setLockType("tp",context);
                    setPattern.setChecked(true);
                }
            }
        });

        setPattern.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    ManageLockType.setLockType("pattern",context);
                    setNormalPIN.setChecked(false);
                }
                if (!b){
                    ManageLockType.setLockType("tp",context);
                    setNormalPIN.setChecked(true);
                }
            }
        });
    }
    private void startMain(){
        Intent i = new Intent(SetLockTypeActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    private void startSetNormalPIN(){
        Intent i = new Intent(SetLockTypeActivity.this,SetNormalPIN.class);
        i.putExtra("try","true");
        startActivity(i);
        finish();
    }
    private void startPattern(){
        Intent i = new Intent(SetLockTypeActivity.this,SetPatternLockActivity.class);
        startActivity(i);
        finish();
    }
    private void showHelpForTimePIN(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setTitle("Time PIN");
        alertDialogBuilder
                .setMessage(getString(R.string.help_time_pin))
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}
