package com.example.yasina.myhalalwat;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class mobileMainActivity extends ActionBarActivity {

    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        Calendar current = Calendar.getInstance();

        TimeZone tz = TimeZone.getDefault();
        String gmt = TimeZone.getTimeZone(tz.getID()).getDisplayName(false,
                TimeZone.SHORT);
        String z1 = gmt.substring(4);

        String z = z1.replaceAll(":", ".");
        double zo = Double.parseDouble(z);
        Log.d("double time", "" + zo);

        List<String> objects = PrayTime.calculatePrayTimes(current,latitude,longitude,zo, PrayTime.CalcMethod.HANAFI);

        for (int i=0; i<objects.size(); i++){
            Log.d("namaz", objects.get(i) + " i");
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
