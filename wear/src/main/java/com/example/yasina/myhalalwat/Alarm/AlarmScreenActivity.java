package com.example.yasina.myhalalwat.Alarm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.yasina.myhalalwat.R;
import com.example.yasina.myhalalwat.Alarm.NamazAlarmContract.Alarm;


public class AlarmScreenActivity extends Activity {

    private TextView namazName, timeNamaz;
    private int hour, min;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_layout);

        namazName = (TextView) findViewById(R.id.namazName_alarmScreen);
        timeNamaz = (TextView) findViewById(R.id.time_alarmScreen);

       /* name = getIntent().getStringExtra(Alarm.COLUMN_NAME_ALARM_NAME);
        hour = getIntent().getIntExtra(Alarm.COLUMN_NAME_ALARM_TIME_HOUR, 0);
        min = getIntent().getIntExtra(Alarm.COLUMN_NAME_ALARM_TIME_MINUTE, 0);*/

        name = "fdf";
        hour = 3;
        min = 1;
        timeNamaz.setText(hour + ":" + min);
        namazName.setText(name);
    }

    public void btnCloseAlarm(View view){
        finish();
    }
}
