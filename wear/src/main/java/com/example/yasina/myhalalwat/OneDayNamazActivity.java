package com.example.yasina.myhalalwat;

        import android.app.Activity;
        import android.app.AlarmManager;
        import android.app.Notification;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
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

public class OneDayNamazActivity extends Activity implements WearableListView.ClickListener{

    private WearableListView mListView;
    private double longitude;
    private double latitude;

    private List<String> namazTimesList;

    private NamazTime namazTime;
    private int pos, hour, min;
    private AlarmDataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_listview);

        dataBase = new AlarmDataBase(this);

                longitude = getIntent().getExtras().getDouble("lo");
        Log.d("nama", longitude + "");
        latitude = getIntent().getExtras().getDouble("la");
        Log.d("nama", latitude + "");


        Calendar current = Calendar.getInstance();

        double zo = PrayTime.getBaseTimeZone();
        Log.d("double time", "" + zo);
      /*  Intent values = new Intent(this, AlarmScreenActivity.class);
        values.putExtra("alarmTheme", "hi");
        PendingIntent pendingIntent = PendingIntent.
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar alarm = Calendar.getInstance();
        alarm.add(Calendar.MINUTE, 1);
       long time = alarm.getTimeInMillis();

        Log.d("alarm",alarm.get(Calendar.HOUR) + " ");
        Log.d("alarm",alarm.get(Calendar.MINUTE) + " ");
        Log.d("alarm",alarm.get(Calendar.DAY_OF_MONTH) + " ");

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                1000 * 20, pendingIntent);*/

      //  namazTimesList = PrayTime.calculatePrayTimes(current, latitude, longitude, zo, PrayTime.CalcMethod.SHAFII);

       dataBase.dropTable();
        namazTimesList = new ArrayList<String>();
        namazTimesList.add("16:19");
        namazTimesList.add("16:20");
        namazTimesList.add("16:21");
        namazTimesList.add("16:22");
        namazTimesList.add("16:23");
        namazTimesList.add("16:24");


      //  namazTime = new NamazTime(listItems.get(pos).toString(), hour, min);

        for (int i=0; i< namazTimesList.size();i++){
            String textOfNamazTime = namazTimesList.get(i);
            StringTokenizer tokenizer = new StringTokenizer(textOfNamazTime,":");
            while (tokenizer.hasMoreElements()) {
                hour = Integer.parseInt(tokenizer.nextToken());
                min = Integer.parseInt(tokenizer.nextToken());
            }
            namazTime = new NamazTime(listItems.get(pos).toString(), hour, min);
            dataBase.addAlarm(namazTime);
        }
        AlarmNamazManager.setAlarms(this);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mListView = (WearableListView) stub.findViewById(R.id.listView1);
              //  mListView.setAdapter(new ShowPrayTimeAdapter(OneDayNamazActivity.this, longitude, latitude));
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

        private String textOfNamazTime;
        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView.findViewById(R.id.textView);
            TextView view2 = (TextView) holder.itemView.findViewById(R.id.textView2);
            final CheckBox checkBox = (CheckBox) holder.itemView.findViewById(R.id.setAlarmCheckBox);
           /* pos = position;
            textOfNamazTime = namazTimesList.get(position);

            checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    if (checkBox.isChecked()) {

                        StringTokenizer tokenizer = new StringTokenizer(textOfNamazTime,":");
                        while (tokenizer.hasMoreElements()) {
                            hour = Integer.parseInt(tokenizer.nextToken());
                            min = Integer.parseInt(tokenizer.nextToken());
                        }

                        namazTime = new NamazTime(listItems.get(pos).toString(), hour, min);
                        //dataBase.addAlarm(namazTime);

                        Toast.makeText(getApplicationContext(), "Checkbox English is True", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Checkbox English is False", Toast.LENGTH_LONG).show();
                    }
                }
            });
            AlarmNamazManager.setAlarms(getApplicationContext());*/
            view.setText(listItems.get(position).toString());
            view2.setText(namazTimesList.get(position));
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }
}