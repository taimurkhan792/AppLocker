package com.electroware.applocker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by user on 9.09.2016.
 */
public class LogsActivity extends AppCompatActivity {

    SaveLogs saveLogs;
    FloatingActionButton deleteLogs;
    ListView log_list;
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.pass_logs);
        saveLogs = new SaveLogs(this);
        log_list = (ListView) findViewById(R.id.log_list);
        String[] logs_array1 = saveLogs.getLogs().split("\n");

        deleteLogs = (FloatingActionButton) findViewById(R.id.deleteLogs);

        log_list.setAdapter(new LogsAdapter(this, logs_array1));

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
