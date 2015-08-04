package com.example.yasina.myhalalwat;


        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.location.Criteria;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.os.Bundle;
        import android.support.wearable.view.WatchViewStub;
        import android.util.Log;
        import android.view.View;
        import android.widget.TextView;

        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.List;
        import java.util.TimeZone;

public class MainActivity extends Activity implements LocationListener{

    private TextView mTextView;
    double longitude = 0;
    double latitude = 0;

    LocationManager lm;
    String provider;
    Location l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        //get location service
        lm=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        Criteria c=new Criteria();
        //criteria object will select best service based on
        //Accuracy, power consumption, response, bearing and monetary cost
        //set false to use best service otherwise it will select the default Sim network
        //and give the location based on sim network
        //now it will first check satellite than Internet than Sim network location
        provider=lm.getBestProvider(c, false);

        l=lm.getLastKnownLocation(provider);
        if(l!=null)
        {
            //get latitude and longitude of the location
            longitude =l.getLongitude();
            latitude =l.getLatitude();

        }

        //
        Calendar current = Calendar.getInstance();

        TimeZone tz = current.getTimeZone();
        String gmt = TimeZone.getTimeZone(tz.getID()).getDisplayName(false,
                TimeZone.SHORT);
        String z1 = gmt.substring(4);

        String z = z1.replaceAll(":", ".");
        double zo = Double.parseDouble(z);
        Log.d("double time", "" + zo);

        List<String> objects = PrayTime.calculatePrayTimes(current,latitude,longitude,zo, PrayTime.CalcMethod.HANAFI);

        for (int i=0; i<objects.size(); i++){
            Log.d("namaz", objects.get(i) + " i");
        }

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
    }

    public void btnSimpleClick(View view){
        Intent intent = new Intent(this, SimpleListActivity.class);
        intent.putExtra("la",latitude);
        intent.putExtra("lo", longitude);
        startActivity(intent);
    }

   /* public void btnAdvancedClick(View view){
        Intent intent = new Intent(this, AdvancedListActivity.class);
        startActivity(intent);
    }*/

    @Override
    public void onLocationChanged(Location arg0)
    {
        double lng=l.getLongitude();
        double lat=l.getLatitude();
    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub
    }
}

