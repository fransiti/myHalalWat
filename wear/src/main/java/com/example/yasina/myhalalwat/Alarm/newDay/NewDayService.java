package com.example.yasina.myhalalwat.Alarm.newDay;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.yasina.myhalalwat.Alarm.AlarmDataBase;
import com.example.yasina.myhalalwat.Alarm.AlarmNamazManager;
import com.example.yasina.myhalalwat.Model.NamazTime;
import com.example.yasina.myhalalwat.OneDayNamazActivity;
import com.example.yasina.myhalalwat.PrayTime;

import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

public class NewDayService extends Service {

    public static String TAG = NewDayService.class.getSimpleName();
    private List<String> namazTimesList;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Calendar calendar = Calendar.getInstance();
        namazTimesList = PrayTime.calculatePrayTimes(calendar, OneDayNamazActivity.latitude, OneDayNamazActivity.longitude, PrayTime.getBaseTimeZone(), PrayTime.CalcMethod.SHAFII);

        AlarmDataBase dbHelper = new AlarmDataBase(this);
        List<NamazTime> alarms = dbHelper.getAlarms();

        for(int i = 0; i < namazTimesList.size(); i++) {
            NamazTime alarm = alarms.get(i);

            String textOfNamazTime = namazTimesList.get(i);
            StringTokenizer tokenizer = new StringTokenizer(textOfNamazTime, ":");

            while (tokenizer.hasMoreElements()) {
                alarm.setHours(Integer.parseInt(tokenizer.nextToken()));
                alarm.setMin(Integer.parseInt(tokenizer.nextToken()));
            }

            Calendar newTime = Calendar.getInstance();
            newTime.set(Calendar.HOUR_OF_DAY, alarm.getHours());
            newTime.set(Calendar.MINUTE, alarm.getMin());

            dbHelper.updateAlarm(alarm);
        }

        AlarmNamazManager.setAlarms(this);

        return 0;
    }
}
