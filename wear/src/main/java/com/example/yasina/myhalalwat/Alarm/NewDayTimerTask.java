package com.example.yasina.myhalalwat.Alarm;


import android.content.Context;

import com.example.yasina.myhalalwat.Model.NamazTime;
import com.example.yasina.myhalalwat.OneDayNamazActivity;
import com.example.yasina.myhalalwat.PrayTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimerTask;

public class NewDayTimerTask  extends TimerTask {

        /*private static ArrayList<String> nextNamazDay;
    static {
        nextNamazDay = new ArrayList<String>();
        nextNamazDay.add("20:30");
        nextNamazDay.add("20:31");
        nextNamazDay.add("20:32");
        nextNamazDay.add("20:33");
        nextNamazDay.add("20:34");
        nextNamazDay.add("20:35");
    }*/
    private List<String> namazTimesList;
    private Context context;

    public NewDayTimerTask(Context context) {
        this.context = context;
    }

    @Override
        public void run() {

        Calendar calendar = Calendar.getInstance();
        namazTimesList = PrayTime.calculatePrayTimes(calendar, OneDayNamazActivity.latitude, OneDayNamazActivity.longitude, PrayTime.getBaseTimeZone(), PrayTime.CalcMethod.SHAFII);

        AlarmDataBase dbHelper = new AlarmDataBase(context);
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

        AlarmNamazManager.setAlarms(context);

        }

}
