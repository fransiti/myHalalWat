package com.example.yasina.myhalalwat.Alarm;

import android.provider.BaseColumns;

public final class NamazAlarmContract  {

    public NamazAlarmContract(){}

    public static abstract class Alarm implements BaseColumns {
        public static final String TABLE_NAME = "namazAlarms";
        public static final String COLUMN_NAME_ALARM_NAME = "name";
        public static final String COLUMN_NAME_ALARM_TIME_HOUR = "hour";
        public static final String COLUMN_NAME_ALARM_TIME_MINUTE = "minute";
    }

}
