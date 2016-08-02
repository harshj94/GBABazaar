package gbabazaar.gbabazaar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoggedIn extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ParseQuery<ParseObject> parseQuery;
    int i, score;
    ParseObject parseObject;
    ArrayList<Item> items;
    ListView listView;
    Item item;
    String cat;
    Timer timer;
    private ItemsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        cat = getIntent().getStringExtra("category");

        items = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list);
        adapter = new ItemsAdapter(getApplicationContext(), items);
        listView.setAdapter(adapter);

        new AdLoadEarly().execute();

        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            Boolean result = new ConnectionDetector(getApplicationContext()).isConnectingToInternet();
                            if (result) {
                                new AdLoad().execute();
                            } else {
                                Toast.makeText(LoggedIn.this, "It seems as if you are not connected to internet.", Toast.LENGTH_SHORT).show();
                            }
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
                Intent it = new Intent(getApplicationContext(), AdDetails.class);
                Item i1 = items.get(i);
                it.putExtra("objectId", i1.gettObjectId());
                it.putExtra("category", cat);
                startActivity(it);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private class AdLoad extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            sharedPreferences = getApplicationContext().getSharedPreferences("MyPreferences", MODE_PRIVATE);
            score = sharedPreferences.getInt(cat, 0);
            ParseObject objectt = null;
            parseQuery = ParseQuery.getQuery("GameScore");
            try {
                objectt = parseQuery.get("ajCfInBYGZ");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert objectt != null;
            if (score != objectt.getInt(cat)) {
                score = objectt.getInt(cat);
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(cat, score);
                editor.apply();
                items.clear();
                List<ParseObject> objects = null;
                parseQuery = ParseQuery.getQuery(cat);
                parseQuery.whereContains("Status","accepted");
                parseQuery.orderByDescending("createdAt");
                parseQuery.setLimit(1000);
                try {
                    objects = parseQuery.find();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                assert objects != null;
                try {
                    ParseObject.unpinAll(cat);
                    ParseObject.pinAll(cat, objects);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                for (i = 0; i < objects.size(); i++) {
                    parseObject = objects.get(i);
                    item = new Item();
                    item.settTitle(parseObject.getString("Title"));
                    item.settCategory(parseObject.getString("Category"));
                    item.settObjectId(parseObject.getObjectId());
                    ParseFile parseFile = parseObject.getParseFile("image0");
                    item.settURL(parseFile.getUrl());
//                    try {
//                        item.settImageBitmap(parseFile.getData());
//                    } catch (ParseException e1) {
//                        e1.printStackTrace();
//                    }
                    items.add(item);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
        }
    }

    private class AdLoadEarly extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(LoggedIn.this, "Loading Ads... Please wait", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            items.clear();
            List<ParseObject> objects = null;
            parseQuery = ParseQuery.getQuery(cat);
            parseQuery.orderByDescending("createdAt");
            parseQuery.fromLocalDatastore();
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
                item.settURL(parseFile.getUrl());
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
