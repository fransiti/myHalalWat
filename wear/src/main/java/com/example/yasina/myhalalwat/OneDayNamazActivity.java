package com.example.yasina.myhalalwat;

        import android.app.Activity;
        import android.content.Context;
        import android.os.Bundle;
        import android.support.wearable.view.WatchViewStub;
        import android.support.wearable.view.WearableListView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.List;

public class OneDayNamazActivity extends Activity implements WearableListView.ClickListener{

    private WearableListView mListView;
    private double longitude;
    private double latitude;

    private List<String> namazTimesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_listview);
        longitude = getIntent().getExtras().getDouble("lo");
        Log.d("nama", longitude + "");
        latitude = getIntent().getExtras().getDouble("la");
        Log.d("nama", latitude + "");


        Calendar current = Calendar.getInstance();

      /*  TimeZone tz = TimeZone.getDefault();
        String gmt = TimeZone.getTimeZone(tz.getID()).getDisplayName(false,
                TimeZone.SHORT);
        String z1 = gmt.substring(4);

        String z = z1.replaceAll(":", ".");*/
        double zo = PrayTime.getBaseTimeZone();
        Log.d("double time", "" + zo);

        namazTimesList = PrayTime.calculatePrayTimes(current, latitude, longitude, zo, PrayTime.CalcMethod.HANAFI);

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

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView.findViewById(R.id.textView);
            TextView view2 = (TextView) holder.itemView.findViewById(R.id.textView2);
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