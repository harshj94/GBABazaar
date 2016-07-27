package gbabazaar.gbabazaar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import rebus.permissionutils.AskagainCallback;
import rebus.permissionutils.FullCallback;
import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionManager;

public class AdUpload extends AppCompatActivity {

    public static ParseObject parseObject;
    TextView adupload, submit, tool_edit, tool_save;
    ArrayList<String> al;
    LinearLayout linearLayout;
    ImageView myImage, back;
    int i;
    String s;
    List<String> path;
    EditText name, mobile, city, title, description, rate;
    MaterialSpinner spinner;
    ParseFile[] file;
    String category;
    ProgressDialog csprogress;
    String object_id, name_, mobile_, city_, title_, description_, rate_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_upload);

        s = "";

        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        adupload = (TextView) findViewById(R.id.hello);
        linearLayout = (LinearLayout) findViewById(R.id.place);
        myImage = (ImageView) findViewById(R.id.placeholder);
        name = (EditText) findViewById(R.id.name);
        mobile = (EditText) findViewById(R.id.mobile);
        city = (EditText) findViewById(R.id.city);
        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        rate = (EditText) findViewById(R.id.rate);
        submit = (TextView) findViewById(R.id.submit);
        tool_edit = (TextView) findViewById(R.id.edit);
        tool_save = (TextView) findViewById(R.id.save);
        back = (ImageView) findViewById(R.id.back);

        spinner.setItems("Agriculture Equipments", "Fruits", "Vegetables", "Auto", "Home/Flat", "Hotels");

        name.setText(ParseUser.getCurrentUser().getString("Name"));
        mobile.setEnabled(false);
        mobile.setText(ParseUser.getCurrentUser().getUsername());

        tool_save.setText("");
        tool_edit.setText("Upload Ad");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        adupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiImageSelector.create(getApplicationContext())
                        .showCamera(true) // show camera or not. true by default
                        .count(3) // max select image size, 9 by default. used width #.multi()
                        .multi() // multi mode, default mode;
                        .origin(al)
                        .start(AdUpload.this, 1);
            }
        });

        category = "Agriculture Equipments";

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                category = item;
            }
        });

        parseObject = new ParseObject("Advertisement");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (al == null) {

                    Toast.makeText(AdUpload.this, "Upload images!!!", Toast.LENGTH_SHORT).show();

                } else if (name.getText().toString().trim().equals("") || city.getText().toString().trim().equals("") || description.getText().toString().trim().equals("") || rate.getText().toString().trim().equals("")) {

                    Toast.makeText(AdUpload.this, "Some of the required field is empty.", Toast.LENGTH_SHORT).show();

                } else {

                    object_id = ParseUser.getCurrentUser().getObjectId();
                    name_ = name.getText().toString().trim();
                    mobile_ = mobile.getText().toString().trim();
                    city_ = city.getText().toString().trim();
                    title_ = title.getText().toString().trim();
                    description_ = description.getText().toString().trim();
                    rate_ = rate.getText().toString().trim();

                    new NetCheck().execute();
                }
            }
        });

        PermissionManager.with(AdUpload.this)
                .permission(PermissionEnum.WRITE_EXTERNAL_STORAGE, PermissionEnum.CAMERA)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                al = (ArrayList<String>) path;
                if (al.size() == 0) {
                    myImage.setVisibility(View.VISIBLE);
                } else {
                    myImage.setVisibility(View.GONE);
                    linearLayout.removeAllViews();
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    lp.gravity = Gravity.NO_GRAVITY;
                    linearLayout.setLayoutParams(lp);
                    for (i = 0; i < al.size(); i++) {
                        File imgFile = new File(path.get(i));
                        if (imgFile.exists()) {
                            try {
                                FileInputStream fis = new FileInputStream(path.get(i));
                                Bitmap imageBitmap = BitmapFactory.decodeStream(fis);
                                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 250, 400, false);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                final ImageView imageView = new ImageView(this);
                                imageView.setId(i);
                                imageView.setPadding(4, 2, 4, 2);
                                imageView.setImageBitmap(imageBitmap);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        Uri imgUri = Uri.parse("file://" + path.get(imageView.getId()));
                                        intent.setDataAndType(imgUri, "image/*");
                                        startActivity(intent);
                                    }
                                });
                                linearLayout.addView(imageView);
                            } catch (Exception ex) {
                                Toast.makeText(getApplicationContext(), "Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        }
    }

    private class NetCheck extends AsyncTask<String, String, Void> {

        Boolean result;

        @Override
        protected void onPreExecute() {
            csprogress = new ProgressDialog(AdUpload.this);
            csprogress.setCancelable(false);
            csprogress.setMessage("Please wait...");
            csprogress.show();
        }

        @Override
        protected Void doInBackground(String... args) {

            ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
            result = connectionDetector.isConnectingToInternet();
            if (result) {

                file = new ParseFile[path.size()];

                for (i = 0; i < path.size(); i++) {
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(path.get(i));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap imageBitmap = BitmapFactory.decodeStream(fis);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] image = stream.toByteArray();
                    file[i] = new ParseFile("image" + i + ".jpeg", image);
                    String s = "image" + i;
                    parseObject.put(s, file[i]);
                }
                parseObject.put("UserObjectId", object_id);
                parseObject.put("Name", name_);
                parseObject.put("Mobile", mobile_);
                parseObject.put("City", city_);
                parseObject.put("Title", title_);
                parseObject.put("Description", description_);
                parseObject.put("Rate", rate_);
                parseObject.put("Category", category);
                parseObject.put("Status", "pending");
                parseObject.put("Images", path.size());

                try {
                    parseObject.save();
                } catch (ParseException e) {
                    s = e.getMessage();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            csprogress.dismiss();
            if (!result) {
                new AlertDialog.Builder(AdUpload.this)
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
            if (s.equals("")) {
                Toast.makeText(getApplicationContext(), "Your ad has been submitted for review.", Toast.LENGTH_LONG).show();
                Intent it = new Intent(AdUpload.this, AdUpload.class);
                startActivity(it);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Error: " + s, Toast.LENGTH_LONG).show();
            }
        }
    }
}