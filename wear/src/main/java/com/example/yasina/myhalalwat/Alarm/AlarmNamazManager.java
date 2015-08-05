package com.example.yasina.myhalalwat.Alarm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.yasina.myhalalwat.Model.NamazTime;
import com.example.yasina.myhalalwat.R;
import com.example.yasina.myhalalwat.Alarm.NamazAlarmContract.Alarm;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yasina on 05.08.15.
 */
public class AlarmNamazManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarms(context);
    }

    public static void setAlarms(Context context) {
        cancelAlarms(context);

        AlarmDataBase dbHelper = new AlarmDataBase(context);
        List<NamazTime> alarms = dbHelper.getAlarms();
        for (NamazTime alarm : alarms) {
Log.d("ala",alarm.getNamazName() + " " + alarm.getHours() + ":" + alarm.getMin());
                PendingIntent pIntent = createPendingIntent(context, alarm);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, alarm.getHours());
                calendar.set(Calendar.MINUTE, alarm.getMin());
                calendar.set(Calendar.SECOND, 00);

                setAlarm(context, calendar, pIntent);
        }

    }

    @SuppressLint("NewApi")
    private static void setAlarm(Context context, Calendar calendar,
                                 PendingIntent pIntent) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pIntent);
            Log.d("ala",calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));
        } else {
            alarmManager.set(android.app.AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pIntent);
        }
    }

    public static void cancelAlarms(Context context) {
        AlarmDataBase dbHelper = new AlarmDataBase(context);
        List<NamazTime> alarms = dbHelper.getAlarms();
        if (alarms != null) {
            for (NamazTime alarm : alarms) {

                    PendingIntent pIntent = createPendingIntent(context, alarm);

                    android.app.AlarmManager alarmManager = (android.app.AlarmManager) context
                            .getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pIntent);

            }
        }
    }

    private static PendingIntent createPendingIntent(Context context,
                                                     NamazTime model) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.putExtra(Alarm._ID, model.getId());
        intent.putExtra(Alarm.COLUMN_NAME_ALARM_NAME, model.getNamazName());
        intent.putExtra(Alarm.COLUMN_NAME_ALARM_TIME_HOUR, model.getHours());
        intent.putExtra(Alarm.COLUMN_NAME_ALARM_TIME_MINUTE, model.getMin());

        return PendingIntent.getService(context, (int) model.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
