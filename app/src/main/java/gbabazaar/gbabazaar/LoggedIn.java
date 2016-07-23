package gbabazaar.gbabazaar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoggedIn extends AppCompatActivity {

    private ItemsAdapter adapter;
    ProgressDialog csprogress;
    SharedPreferences sharedPreferences;
    ParseQuery<ParseObject> parseQuery;
    int i, score;
    ParseObject parseObject;
    ArrayList<Item> items;
    ListView listView;
    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        items = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list);
        adapter = new ItemsAdapter(getApplicationContext(), items);
        listView.setAdapter(adapter);

        new AdLoadEarly().execute();

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
                Intent it = new Intent(getApplicationContext(), AdDetails.class);
                Item i1 = items.get(i);
                it.putExtra("objectId", i1.gettObjectId());
                startActivity(it);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(LoggedIn.this, AdUpload.class);
                startActivity(it);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logged_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            csprogress = new ProgressDialog(LoggedIn.this);
            csprogress.show();
            csprogress.setCancelable(false);
            csprogress.setTitle("Logging Out");
            csprogress.setMessage("Please wait...");
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    csprogress.dismiss();
                    if (e != null) {
                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Intent it = new Intent(LoggedIn.this, MainActivity.class);
                        startActivity(it);
                        finish();
                    }
                }
            });

            return true;
        }
        if (id == R.id.action_profile) {
            Intent it = new Intent(LoggedIn.this, Profile.class);
            startActivity(it);
            return true;
        }

        if (id == R.id.action_myads) {
            Intent it = new Intent(LoggedIn.this, MyAdsList.class);
            startActivity(it);
            return true;
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
            score = sharedPreferences.getInt("score", 0);
            ParseObject objectt = null;
            parseQuery = ParseQuery.getQuery("GameScore");
            try {
                objectt = parseQuery.get("ajCfInBYGZ");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert objectt != null;
            if (score != objectt.getInt("score")) {
                score = objectt.getInt("score");
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("score", score);
                editor.apply();
                items.clear();
                List<ParseObject> objects = null;
                parseQuery = ParseQuery.getQuery("Advertisement");
                parseQuery.orderByDescending("createdAt");
                parseQuery.setLimit(1000);
                try {
                    objects = parseQuery.find();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                assert objects != null;
                try {
                    ParseObject.unpinAll("Advertisements");
                    ParseObject.pinAll("Advertisements", objects);
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
                    try {
                        item.settImageBitmap(parseFile.getData());
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
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
        }

        @Override
        protected Void doInBackground(Void... voids) {
            items.clear();
            List<ParseObject> objects = null;
            parseQuery = ParseQuery.getQuery("Advertisement");
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
            Toast.makeText(LoggedIn.this, "Please wait... Ads are being loaded.", Toast.LENGTH_SHORT).show();
        }
    }
}
