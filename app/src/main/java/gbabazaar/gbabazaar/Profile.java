package gbabazaar.gbabazaar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by harsh on 08-Jul-16.
 */
public class Profile extends AppCompatActivity {

    EditText name, mobile, email;
    String name_, mobile_, email_;
    TextView save;
    ImageView back;
    ParseUser parseUser;
    ProgressDialog csprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        new NetCheck().execute();

        name = (EditText) findViewById(R.id.name);
        mobile = (EditText) findViewById(R.id.mobile);
        email = (EditText) findViewById(R.id.emailid);
        save = (TextView) findViewById(R.id.save);
        back = (ImageView) findViewById(R.id.back);
        mobile.setEnabled(false);
        parseUser = ParseUser.getCurrentUser();
        name_ = parseUser.getString("Name");
        mobile_ = parseUser.getUsername();
        try {
            email_ = parseUser.getEmail();
        } catch (Exception e) {
            email_ = "";
        }
        name.setText(name_);
        mobile.setText(mobile_);
        email.setText(email_);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean r=new ConnectionDetector(getApplicationContext()).isConnectingToInternet();
                if(r) {
                    parseUser.put("Name", name.getText().toString().trim());
                    parseUser.setEmail(email.getText().toString().trim());
                    csprogress = new ProgressDialog(Profile.this);
                    csprogress.show();
                    csprogress.setCancelable(false);
                    csprogress.setMessage("Please wait...");
                    parseUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            csprogress.dismiss();
                            if (e != null) {
                                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(Profile.this, "It seems as if you are not connected to internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class NetCheck extends AsyncTask<Void, Void, Void> {
        Boolean result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            csprogress = new ProgressDialog(Profile.this);
            csprogress.show();
            csprogress.setCancelable(false);
            csprogress.setMessage("Please wait...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
            result = connectionDetector.isConnectingToInternet();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            csprogress.dismiss();
            if (!result) {
                new AlertDialog.Builder(Profile.this)
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
