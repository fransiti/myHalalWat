package com.example.yasina.myhalalwat;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yasina.myhalalwat.Alarm.AlarmDataBase;
import com.example.yasina.myhalalwat.Alarm.AlarmNamazManager;
import com.example.yasina.myhalalwat.Alarm.newDay.NewDayService;
import com.example.yasina.myhalalwat.Model.Mazhab;
import com.example.yasina.myhalalwat.Model.NamazTime;
import com.example.yasina.myhalalwat.View.PrayerTimelineView;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import android.location.Geocoder;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by yasina on 10.08.15.
 */
public class New_OneDayNamazActivity extends Activity implements LocationListener, View.OnClickListener {

    private TextView dayOfWeek,dayOfMonth, namaz_city, fadjr, sunrise, zyhr, magrib, asr, isha, hanafit, shafit;
    private ImageView fadjrAlarm, sunriseAlarm, zyhrAlarm, magribAlarm, asrAlarm, ishaAlarm;
    private PrayerTimelineView prayerTimelineView;

    public static double longitude,latitude;

    private List<String> namazTimesList;

    private NamazTime namazTime;
    private int pos, hour, min;
    private AlarmDataBase dataBase;
    private String textOfNamazTime, provider;

    public static LocationManager locationManager;
    public static Location location;

    private void setNewDayAlarm(){

        Intent intent = new Intent(this, NewDayService.class);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        Log.d("ala2", "create pending");
        PendingIntent pendingIntent = PendingIntent.getService(this, 1234, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (android.app.AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    private void init(){
        prayerTimelineView = (PrayerTimelineView) findViewById(R.id.namaz_timeline);
        prayerTimelineView.setTime(Calendar.getInstance());
        prayerTimelineView.setUp(new int[]{300, 500, 700, 900, 1100, 1300});

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        String fnialAddress = "";
        latitude = 55.75;
        longitude = 49.1333333;
      /*  try {
            List<Address> address = geoCoder.getFromLocation(latitude, longitude, 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();
            for (int i=0; i<maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                builder.append(" ");
            }
            fnialAddress = builder.toString(); //This is the complete address.
        } catch (IOException e) {}
        catch (NullPointerException e) {}*/

        dayOfWeek = (TextView) findViewById(R.id.dayOfWeek_info);
        dayOfMonth = (TextView) findViewById(R.id.dayOfMonth_info);
        namaz_city = (TextView) findViewById(R.id.namaz_city);
      //  namaz_city.setText(fnialAddress);
        fadjr = (TextView) findViewById(R.id.fadjr_namaz_time);
        sunrise = (TextView) findViewById(R.id.sunrise_time);
        zyhr = (TextView) findViewById(R.id.zyhr_time);
        asr = (TextView) findViewById(R.id.asr_time);
        magrib = (TextView) findViewById(R.id.magrib_time);
        isha = (TextView) findViewById(R.id.isha_time);

        hanafit = (TextView) findViewById(R.id.namazes_calculated_by);
        hanafit.setOnClickListener(this);
        shafit = (TextView) findViewById(R.id.switch_to_another_calc_method);
        shafit.setOnClickListener(this);

        fadjrAlarm = (ImageView) findViewById(R.id.fadjr_alarm);
        sunriseAlarm = (ImageView) findViewById(R.id.sunrise_alarm);
        zyhrAlarm = (ImageView) findViewById(R.id.zyhr_alarm);
        asrAlarm = (ImageView) findViewById(R.id.asr_alarm);
        magribAlarm = (ImageView) findViewById(R.id.magrib_alarm);
        ishaAlarm = (ImageView) findViewById(R.id.isha_alarm);

        setNamazTimes();

        setImage(fadjrAlarm,dataBase.getAlarm("Fajr").isEnabled);
        setImage(sunriseAlarm,dataBase.getAlarm("Sunrise").isEnabled);
        setImage(zyhrAlarm, dataBase.getAlarm("Dhuhr").isEnabled);
        setImage(asrAlarm, dataBase.getAlarm("Asr").isEnabled);
        setImage(magribAlarm, dataBase.getAlarm("Magrib").isEnabled);
        setImage(ishaAlarm, dataBase.getAlarm("Isha").isEnabled);

        Mazhab mazhab = dataBase.getMazhab();
        PrayTime.CalcMethod calcMethod;
        if(mazhab.isHanafi()) hanafit.setTextColor(R.color.halal_green);
        else shafit.setTextColor(R.color.halal_green);
    }

    private void setNamazTimes(){
        fadjr.setText(namazTimesList.get(0));
        sunrise.setText(namazTimesList.get(1));
        zyhr.setText(namazTimesList.get(2));
        asr.setText(namazTimesList.get(3));
        magrib.setText(namazTimesList.get(4));
        isha.setText(namazTimesList.get(5));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.namaz_times);
        dataBase = new AlarmDataBase(this);
        dataBase.updateMazhab(new Mazhab(true));
        Calendar current = Calendar.getInstance();
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        Criteria c=new Criteria();
        provider= locationManager.getBestProvider(c, false);

        location = locationManager.getLastKnownLocation(provider);
        if(location != null)
        {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        Log.d("timezone",PrayTime.getBaseTimeZone() + "");
        Mazhab mazhab = dataBase.getMazhab();
        PrayTime.CalcMethod calcMethod;
        if(mazhab.isHanafi()) calcMethod = PrayTime.CalcMethod.HANAFI;
        else calcMethod = PrayTime.CalcMethod.SHAFII;
        namazTimesList = PrayTime.calculatePrayTimes(current, latitude, longitude, PrayTime.getBaseTimeZone(),calcMethod);

        init();

        Locale locale = Locale.getDefault();

        dayOfWeek.setText(current.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale));
        dayOfMonth.setText(current.get(Calendar.DAY_OF_MONTH) + " " + current.getDisplayName(Calendar.MONTH, Calendar.LONG, locale));

        setNewDayAlarm();
    }

    @Override
    public void onLocationChanged(Location location) {

        double lng= this.location.getLongitude();
        double lat= this.location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.fadjr_alarm:
                setImage(fadjrAlarm, dataBase.getAlarm("Fajr").isEnabled ? false : true);
                break;
            case R.id.sunrise_alarm:
                setImage(sunriseAlarm, dataBase.getAlarm("Sunrise").isEnabled ? false : true);
                break;
            case R.id.zyhr_alarm:
                setImage(zyhrAlarm, dataBase.getAlarm("Dhuhr").isEnabled ? false : true);
                break;
            case R.id.asr_alarm:
                setImage(asrAlarm, dataBase.getAlarm("Asr").isEnabled ? false : true);
                break;
            case R.id.magrib_alarm:
                setImage(magribAlarm, dataBase.getAlarm("Magrib").isEnabled ? false : true);
                break;
            case R.id.isha_alarm:
                setImage(ishaAlarm, dataBase.getAlarm("Isha").isEnabled ? false : true);
                break;
            case R.id.namazes_calculated_by:
                if(!dataBase.getMazhab().isHanafi()) {
                    setMazhab(false, R.color.halal_green, R.color.black, PrayTime.CalcMethod.HANAFI);
                }

                break;
            case R.id.switch_to_another_calc_method:
                if(dataBase.getMazhab().isHanafi()) {
                    setMazhab(true, R.color.black, R.color.halal_green, PrayTime.CalcMethod.SHAFII);
                }
                break;

        }
        AlarmNamazManager.setAlarms(this);
    }

    private void setMazhab(boolean mazhab, int colorWin, int colorLose,PrayTime.CalcMethod calcMethod ){
            dataBase.updateMazhab(new Mazhab(!mazhab));
            hanafit.setTextColor(colorWin);
            shafit.setTextColor(colorLose);
            namazTimesList = PrayTime.calculatePrayTimes(Calendar.getInstance(), latitude, longitude, PrayTime.getBaseTimeZone(), calcMethod);
            setNamazTimes();
            dataBase.close();
    }

    private void setImage(ImageView imageView, boolean isEnabled){
        if(isEnabled) imageView.setImageResource(R.drawable.namaz_setup);
        else imageView.setImageResource(R.drawable.namaz_remove);
    }
}
