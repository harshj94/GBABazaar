package gbabazaar.gbabazaar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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

    ProgressDialog csprogress;
    SharedPreferences sharedPreferences;
    ParseQuery<ParseObject> parseQuery;
    int i, score;
    ParseObject parseObject;
    ArrayList<Item> items;
    ListView listView;
    Item item;
    private ItemsAdapter adapter;

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

        Boolean result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            csprogress = new ProgressDialog(LoggedIn.this);
            csprogress.show();
            csprogress.setCancelable(false);
            csprogress.setMessage("Please wait...");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
            result = connectionDetector.isConnectingToInternet();
            if (result) {
                items.clear();
                List<ParseObject> objects = null;
                parseQuery = ParseQuery.getQuery("Advertisement");
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
            csprogress.dismiss();
            if (!result) {
                new AlertDialog.Builder(LoggedIn.this)
                        .setTitle("Internet Connection Error")
                        .setCancelable(false)
                        .setMessage("It seems as if you are not connected to internet")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new AdLoadEarly().execute();
                            }
                        })
                        .show();
            }
            adapter.notifyDataSetChanged();
            Toast.makeText(LoggedIn.this, "Please wait... Ads are being loaded.", Toast.LENGTH_SHORT).show();
        }
    }
}
