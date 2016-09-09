package com.electroware.applocker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by user on 9.09.2016.
 */
public class LogsActivity extends AppCompatActivity {
    TextView showLogs;
    SaveLogs saveLogs;
    FloatingActionButton deleteLogs;
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.pass_logs);
        saveLogs = new SaveLogs(this);
        showLogs = (TextView) findViewById(R.id.showLogs);
        deleteLogs = (FloatingActionButton) findViewById(R.id.deleteLogs);
        showLogs.setText(saveLogs.getLogs());
        deleteLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteLogsNow();
            }
        });
    }
    private void deleteLogsNow(){
        saveLogs.deleteLogs();
        finish();
    }
}
