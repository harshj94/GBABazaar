package gbabazaar.gbabazaar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyAdsList extends AppCompatActivity {

    ParseQuery<ParseObject> parseQuery;
    int i;
    ParseObject parseObject;
    ArrayList<Item> items;
    ListView listView;
    Item item;
    private ItemsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        items = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list);
        adapter = new ItemsAdapter(getApplicationContext(), items);
        listView.setAdapter(adapter);

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new AdLoad().execute();
                        } catch (Exception ignored) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 60000);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent it = new Intent(getApplicationContext(), MyAdDetails.class);
//                Item i1 = items.get(i);
//                it.putExtra("objectId", i1.gettObjectId());
//                startActivity(it);
            }
        });

    }

    private class AdLoad extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            items.clear();
            List<ParseObject> objects = null;
            parseQuery = ParseQuery.getQuery("Advertisement");
            parseQuery.whereContains("UserObjectId", ParseUser.getCurrentUser().getObjectId());
            parseQuery.orderByDescending("createdAt");
            parseQuery.setLimit(1000);
            try {
                objects = parseQuery.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert objects != null;
            for (i = 0; i < objects.size(); i++) {
                parseObject = objects.get(i);
                item = new Item();
                item.settTitle(parseObject.getString("Title"));
                item.settCategory(parseObject.getString("Category"));
                item.settObjectId(parseObject.getObjectId());
                ParseFile parseFile = parseObject.getParseFile("image0");
                try {
                    item.settImageBitmap(parseFile.getData());
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                items.add(item);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
        }
    }
}
