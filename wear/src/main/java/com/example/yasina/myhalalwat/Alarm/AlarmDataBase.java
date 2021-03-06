package com.example.yasina.myhalalwat.Alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.yasina.myhalalwat.Alarm.NamazAlarmContract.Alarm;
import com.example.yasina.myhalalwat.Model.Mazhab;
import com.example.yasina.myhalalwat.Model.NamazTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yasina on 05.08.15.
 */
public class AlarmDataBase extends SQLiteOpenHelper {

  // public static final String TABLE_NAME = "namazlar";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "namaz.db";

    private static final String SQL_CREATE_TABLE_NAMAZLAR = "CREATE TABLE namazlar"
             + " (" + Alarm._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + Alarm.COLUMN_NAME_ALARM_NAME + " TEXT,"
            + Alarm.COLUMN_NAME_ALARM_TIME_HOUR + " INTEGER,"
            + Alarm.COLUMN_NAME_ALARM_TIME_MINUTE + " INTEGER,"
            + Alarm.COLUMN_NAME_ALARM_ENABLED + " BOOLEAN"
            + " )";

    private static final String SQL_CREATE_TABLE_MAZHAB = "CREATE TABLE mazhab"
            + " (mazhab_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " hanafi BOOLEAN)";


    private static final String SQL_DELETE_ALARM = "DROP TABLE IF EXISTS "
            + Alarm.TABLE_NAME;

    public AlarmDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_NAMAZLAR);
        db.execSQL(SQL_CREATE_TABLE_MAZHAB);

        ContentValues values = new ContentValues();
        values.put("hanafi", true);
        getWritableDatabase().insert("mazhab", null, values);

        db.insert(Alarm.TABLE_NAME, null, createContentValue(new NamazTime("Fajr", 0, 0, false)));
        db.insert(Alarm.TABLE_NAME, null, createContentValue(new NamazTime("Sunrise", 0, 0, false)));
        db.insert(Alarm.TABLE_NAME, null, createContentValue(new NamazTime("Dhuhr", 0, 0, false)));
        db.insert(Alarm.TABLE_NAME, null, createContentValue(new NamazTime("Asr", 0, 0, false)));
        db.insert(Alarm.TABLE_NAME, null, createContentValue(new NamazTime("Magrib", 0, 0, false)));
        db.insert(Alarm.TABLE_NAME, null, createContentValue(new NamazTime("Isha", 0, 0, false)));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ALARM);
        onCreate(db);
    }

    public void dropTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE " + Alarm.TABLE_NAME);
    }

    public int updateMazhab(Mazhab mazhab){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("hanafi", mazhab.isHanafi());
        int id = mazhab.getId();
        return getWritableDatabase().update("mazhab", values, "mazhab_id" + " =?" , new String[] { String.valueOf(id) });
    }

    public Mazhab getMazhab(){
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM mazhab WHERE mazhab_id=1";

        Cursor c = db.rawQuery(select, null);

        if (c.moveToNext()) {
            Mazhab mazhab = new Mazhab();
            mazhab.setHanafi(c.getInt(c.getColumnIndex("hanafi")) == 0 ? false : true);
            return mazhab;
        }

        return null;
    }

    private NamazTime cursorToNamazTime(Cursor c) {
        NamazTime namaz = new NamazTime();
        namaz.setId(c.getInt(c.getColumnIndex(Alarm._ID)));
        namaz.setNamazName(c.getString(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_NAME)));
        namaz.setHours(c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TIME_HOUR)));
        namaz.setMin(c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TIME_MINUTE)));
        namaz.setIsEnabled(c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_ENABLED)) == 0 ? false : true);
        return namaz;
    }

    private ContentValues createContentValue(NamazTime namazTime) {
        ContentValues values = new ContentValues();
        values.put(Alarm.COLUMN_NAME_ALARM_NAME, namazTime.getNamazName());
        values.put(Alarm.COLUMN_NAME_ALARM_TIME_HOUR, namazTime.getHours());
        values.put(Alarm.COLUMN_NAME_ALARM_TIME_MINUTE, namazTime.getMin());
        values.put(Alarm.COLUMN_NAME_ALARM_ENABLED, namazTime.isEnabled);
        return values;
    }

    public long addAlarm(NamazTime namazs) {
        ContentValues values = createContentValue(namazs);
        return getWritableDatabase().insert(Alarm.TABLE_NAME, null, values);
    }

    public NamazTime getAlarm(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + Alarm.TABLE_NAME + " WHERE "
                + Alarm.COLUMN_NAME_ALARM_NAME + " ='" + name + "'";

        Cursor c = db.rawQuery(select, null);

        if (c.moveToNext()) {
            return cursorToNamazTime(c);
        }

        return null;
    }

    public long updateAlarm(NamazTime namaz) {
        ContentValues values = createContentValue(namaz);
        return getWritableDatabase().update(Alarm.TABLE_NAME, values,
                Alarm.COLUMN_NAME_ALARM_NAME + " =?" , new String[] { String.valueOf(namaz.getNamazName()) });
    }

    public int deleteAlarm(long id) {
        return getWritableDatabase().delete(Alarm.TABLE_NAME,
                Alarm._ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<NamazTime> getAlarms() {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + Alarm.TABLE_NAME;

        Cursor c = db.rawQuery(select, null);

        List<NamazTime> alarmList = new ArrayList<NamazTime>();

        while (c.moveToNext()) {
            alarmList.add(cursorToNamazTime(c));
        }

        if (!alarmList.isEmpty()) {
            return alarmList;
        }

        return null;
    }

}


