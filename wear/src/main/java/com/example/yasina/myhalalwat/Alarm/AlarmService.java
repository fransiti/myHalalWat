package com.example.yasina.myhalalwat.Alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by yasina on 05.08.15.
 */
public class AlarmService extends Service {

    public static String TAG = AlarmService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent alarmIntent = new Intent(getBaseContext(), AlarmScreenActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmIntent.putExtras(intent);
        getApplication().startActivity(alarmIntent);

        AlarmNamazManager.setAlarms(this);

        return super.onStartCommand(intent, flags, startId);
    }

}


