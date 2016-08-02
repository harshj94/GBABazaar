package gbabazaar.gbabazaar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import rebus.permissionutils.AskagainCallback;
import rebus.permissionutils.FullCallback;
import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionManager;

public class MyAdDetails extends AppCompatActivity {

    TextView title, category, rate, description, name, city, call, edit, save;
    ParseQuery<ParseObject> parseQuery;
    String objectId;
    ParseObject parseObject;
    byte[][] bytes;
    LinearLayout linearLayout;
    ProgressDialog csprogress;
    int images, i;
    ImageView imageView;
    Boolean result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_details);
        call = (TextView) findViewById(R.id.call);
        call.setText("Delete");
        objectId = getIntent().getStringExtra("objectId");
        linearLayout = (LinearLayout) findViewById(R.id.place);
        imageView = (ImageView) findViewById(R.id.back);
        edit = (TextView) findViewById(R.id.edit);
        save = (TextView) findViewById(R.id.save);
        edit.setText("Ad View");
        save.setText("");
        new NetCheck().execute();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        PermissionManager.with(MyAdDetails.this)
                .permission(PermissionEnum.WRITE_EXTERNAL_STORAGE)
                .askagain(true)
                .askagainCallback(new AskagainCallback() {
                    @Override
                    public void showRequestPermission(UserResponse response) {
                        showDialog(1);
                    }
                })
                .callback(new FullCallback() {
                    @Override
                    public void result(ArrayList<PermissionEnum> permissionsGranted, ArrayList<PermissionEnum> permissionsDenied, ArrayList<PermissionEnum> permissionsDeniedForever, ArrayList<PermissionEnum> permissionsAsked) {
                    }
                })
                .ask();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handleResult(requestCode, permissions, grantResults);
    }

    private class FetchAdDetails extends AsyncTask<Void, Void, Void> {

        Boolean result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            csprogress = new ProgressDialog(MyAdDetails.this);
            csprogress.setCancelable(false);
            csprogress.setMessage("Please wait...");
            csprogress.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
            result = connectionDetector.isConnectingToInternet();
            if (result) {
                parseQuery = ParseQuery.getQuery(getIntent().getStringExtra("category"));
                parseQuery.orderByDescending("createdAt");
                try {
                    parseObject = parseQuery.get(objectId);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                images = parseObject.getInt("Images");
                int j;
                bytes = new byte[images][];
                for (j = 0; j < images; j++) {
                    ParseFile parseFile = parseObject.getParseFile("image" + j + "");
                    try {
                        bytes[j] = parseFile.getData();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            csprogress.dismiss();
            if (!result) {
                new AlertDialog.Builder(MyAdDetails.this)
                        .setTitle("Internet Connection Error")
                        .setCancelable(false)
                        .setMessage("It seems as if you are not connected to internet")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new FetchAdDetails().execute();
                            }
                        })
                        .show();
            }
            title = (TextView) findViewById(R.id.title);
            category = (TextView) findViewById(R.id.category);
            rate = (TextView) findViewById(R.id.rate);
            description = (TextView) findViewById(R.id.description);
            name = (TextView) findViewById(R.id.name);
            city = (TextView) findViewById(R.id.city);

            title.setText("Title: " + parseObject.getString("Title"));
            category.setText("Category: " +parseObject.getString("Category"));
            rate.setText("Rate: "+parseObject.getString("Rate"));
            description.setText("Description: "+parseObject.getString("Description"));
            name.setText("Name: "+parseObject.getString("Name"));
            city.setText("City: "+parseObject.getString("City"));

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.NO_GRAVITY;
            linearLayout.setLayoutParams(lp);
            linearLayout.removeAllViews();

            for (i = 0; i < images; i++) {
                final ImageView imageView = new ImageView(MyAdDetails.this);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes[i], 0, bytes[i].length);
                bmp = Bitmap.createScaledBitmap(bmp, 250, 400, false);
                imageView.setPadding(4, 2, 4, 2);
                imageView.setImageBitmap(bmp);
                imageView.setId(i);
                linearLayout.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Time now = new Time();
                        now.setToNow();
                        String s = now.toString();
                        s = s.substring(0, Math.min(s.length(), 15));
                        File f = new File(Environment.getExternalStorageDirectory() + File.separator + s);
                        try {
                            f.createNewFile();
                            FileOutputStream fo = new FileOutputStream(f);
                            fo.write(bytes[imageView.getId()]);
                            fo.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Uri path = Uri.fromFile(f);
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(path, "image/*");
                        startActivity(intent);
                    }
                });
            }

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(MyAdDetails.this)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new DeleteAd().execute();
                                }
                            })
                            .show();
                }
            });

        }
    }

    private class DeleteAd extends AsyncTask<Void, Void, Void> {
        Boolean result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            csprogress = new ProgressDialog(MyAdDetails.this);
            csprogress.setCancelable(false);
            csprogress.setMessage("Please wait...");
            csprogress.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
            result = connectionDetector.isConnectingToInternet();
            if (result) {
                try {
                    parseObject.delete();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            csprogress.dismiss();
            if (!result) {
                new AlertDialog.Builder(MyAdDetails.this)
                        .setTitle("Internet Connection Error")
                        .setCancelable(false)
                        .setMessage("It seems as if you are not connected to internet")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new DeleteAd().execute();
                            }
                        })
                        .show();
            }
            Toast.makeText(MyAdDetails.this, "Your ad will be deleted soon. Thank You.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class NetCheck extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            result = new ConnectionDetector(MyAdDetails.this).isConnectingToInternet();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (result) {
                new FetchAdDetails().execute();
            } else {
                new AlertDialog.Builder(MyAdDetails.this)
                        .setTitle("Internet Connection Error")
                        .setCancelable(false)
                        .setMessage("It seems as if you are not connected to internet")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new NetCheck().execute();
                            }
                        })
                        .show();
            }
        }
    }
}
