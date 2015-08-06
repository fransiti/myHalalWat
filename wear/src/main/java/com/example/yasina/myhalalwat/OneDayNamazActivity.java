package com.example.yasina.myhalalwat;

        import android.app.Activity;
        import android.app.AlarmManager;
        import android.app.Notification;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.location.Criteria;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.os.Bundle;
        import android.os.SystemClock;
        import android.support.v4.app.NotificationManagerCompat;
        import android.support.wearable.view.WatchViewStub;
        import android.support.wearable.view.WearableListView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.CheckBox;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.yasina.myhalalwat.Alarm.AlarmDataBase;
        import com.example.yasina.myhalalwat.Alarm.AlarmNamazManager;
        import com.example.yasina.myhalalwat.Alarm.AlarmScreenActivity;
        import com.example.yasina.myhalalwat.Model.NamazTime;

        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.List;
        import java.util.StringTokenizer;
        import java.util.TimeZone;

public class OneDayNamazActivity extends Activity implements WearableListView.ClickListener,LocationListener {

    private WearableListView mListView;
    public static double longitude,latitude;

    private List<String> namazTimesList, nextNamazDay;

    private NamazTime namazTime;
    private int pos, hour, min;
    private AlarmDataBase dataBase;
    private String textOfNamazTime;


    LocationManager lm;
    String provider;
    Location l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_listview);

        dataBase = new AlarmDataBase(this);

        lm=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        Criteria c=new Criteria();
        provider=lm.getBestProvider(c, false);

        l=lm.getLastKnownLocation(provider);
        if(l!=null)
        {
            longitude =l.getLongitude();
            latitude =l.getLatitude();
        }

        Calendar current = Calendar.getInstance();
        namazTimesList = PrayTime.calculatePrayTimes(current, latitude, longitude, PrayTime.getBaseTimeZone(), PrayTime.CalcMethod.SHAFII);

//       dataBase.dropTable();

      /*  namazTimesList = new ArrayList<String>();
        namazTimesList.add("20:26");
        namazTimesList.add("20:27");
        namazTimesList.add("19:37");
        namazTimesList.add("19:38");
        namazTimesList.add("19:39");
        namazTimesList.add("19:40");*/


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mListView = (WearableListView) stub.findViewById(R.id.listView1);
                mListView.setAdapter(new MyAdapter(OneDayNamazActivity.this));
                mListView.setClickListener(OneDayNamazActivity.this);
            }
        });
    }

    private static ArrayList<String> listItems;
    static {
        listItems = new ArrayList<String>();
        listItems.add("Fajr");
        listItems.add("Sunrise");
        listItems.add("Dhuhr");
        listItems.add("Asr");
        listItems.add("Magrib");
        listItems.add("Isha");
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {

    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    @Override
    public void onLocationChanged(Location location) {

        double lng=l.getLongitude();
        double lat=l.getLatitude();
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

    private class MyAdapter extends WearableListView.Adapter {
        private final LayoutInflater mInflater;

        private MyAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WearableListView.ViewHolder(
                    mInflater.inflate(R.layout.row_simple_item_layout, null));
        }


        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView.findViewById(R.id.textView);
            TextView view2 = (TextView) holder.itemView.findViewById(R.id.textView2);
            final CheckBox checkBox = (CheckBox) holder.itemView.findViewById(R.id.setAlarmCheckBox);

            pos = position;
            textOfNamazTime = namazTimesList.get(position);
           // namazTime = new NamazTime(listItems.get(pos).toString(), hour, min, false);
            //Toast.makeText(getApplicationContext(), "not checked", Toast.LENGTH_LONG).show();

            StringTokenizer tokenizer = new StringTokenizer(textOfNamazTime,":");
            while (tokenizer.hasMoreElements()) {
                hour = Integer.parseInt(tokenizer.nextToken());
                min = Integer.parseInt(tokenizer.nextToken());
            }

            holder.itemView.findViewById(R.id.setAlarmCheckBox).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(checkBox.isChecked()) {
                        namazTime = new NamazTime(listItems.get(pos).toString(), hour, min, true);
                        Toast.makeText(getApplicationContext(), "isChecked", Toast.LENGTH_LONG).show();
                    }else{
                        namazTime = new NamazTime(listItems.get(pos).toString(), hour, min, false);
                        Toast.makeText(getApplicationContext(), "not checked", Toast.LENGTH_LONG).show();
                    }
                    dataBase.updateAlarm(namazTime);
                }
            });

            view.setText(listItems.get(position).toString());
            view2.setText(namazTimesList.get(position));
            checkBox.setChecked(dataBase.getAlarm(listItems.get(position).toString()).isEnabled());
            Log.d("ala", listItems.get(position).toString() + " " + dataBase.getAlarm(listItems.get(position).toString()).isEnabled());
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }

    }

    public void btnSetAlarm(View view){
        AlarmNamazManager.setAlarms(getApplicationContext());
    }
}