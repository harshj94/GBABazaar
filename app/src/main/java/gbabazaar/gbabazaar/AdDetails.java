package gbabazaar.gbabazaar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AdDetails extends AppCompatActivity {

    TextView title, category, rate, description, name, city, call, edit, save;
    ParseQuery<ParseObject> parseQuery;
    String objectId;
    ParseObject parseObject;
    byte[][] bytes;
    LinearLayout linearLayout;
    ProgressDialog csprogress;
    int images, i;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_details);
        objectId = getIntent().getStringExtra("objectId");
        linearLayout = (LinearLayout) findViewById(R.id.place);
        imageView = (ImageView) findViewById(R.id.back);
        edit = (TextView) findViewById(R.id.edit);
        save = (TextView) findViewById(R.id.save);
        edit.setText("Ad View");
        save.setText("");
        new FetchAdDetails().execute();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private class FetchAdDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            csprogress = new ProgressDialog(AdDetails.this);
            csprogress.setCancelable(false);
            csprogress.setMessage("Please wait...");
            csprogress.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            parseQuery = ParseQuery.getQuery("Advertisement");
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            title = (TextView) findViewById(R.id.title);
            category = (TextView) findViewById(R.id.category);
            rate = (TextView) findViewById(R.id.rate);
            description = (TextView) findViewById(R.id.description);
            name = (TextView) findViewById(R.id.name);
            city = (TextView) findViewById(R.id.city);
            call = (TextView) findViewById(R.id.call);

            title.setText(parseObject.getString("Title"));
            category.setText(parseObject.getString("Category"));
            rate.setText(parseObject.getString("Rate"));
            description.setText(parseObject.getString("Description"));
            name.setText(parseObject.getString("Name"));
            city.setText(parseObject.getString("City"));

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.NO_GRAVITY;
            linearLayout.setLayoutParams(lp);
            linearLayout.removeAllViews();

            for (i = 0; i < images; i++) {
                final ImageView imageView = new ImageView(AdDetails.this);
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
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + parseObject.getString("Mobile")));
                    startActivity(intent);
                }
            });

            csprogress.dismiss();
        }
    }
}
