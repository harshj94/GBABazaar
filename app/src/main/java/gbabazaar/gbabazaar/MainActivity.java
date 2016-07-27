package gbabazaar.gbabazaar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.stephentuso.welcome.WelcomeScreenHelper;

import custom_font.MyTextView;

public class MainActivity extends AppCompatActivity {
    TextView holliday, needhelp;
    EditText username, password;
    TextView login;
    MyTextView create;
    ProgressDialog csprogress;
    WelcomeScreenHelper welcomeScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeScreen = new WelcomeScreenHelper(this, MyWelcomeActivity.class);
        welcomeScreen.show(savedInstanceState);

        new Login().execute();

        create = (MyTextView) findViewById(R.id.create);
        holliday = (TextView) findViewById(R.id.holliday);
        needhelp = (TextView) findViewById(R.id.needhelp);

        needhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Contact Us")
                        .setMessage("Email:\tgbabazaar@gmail.com\nMobile:\t+91 9960926981")
                        .setNegativeButton("Call Us", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:+919960926981"));
                                startActivity(intent);
                            }
                        })
                        .setNeutralButton("Email Us", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "gbabazaar@gmail.com", null));
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                            }
                        })
                        .show();
            }
        });

        Typeface custom_fonts = Typeface.createFromAsset(getAssets(), "fonts/ArgonPERSONAL-Regular.otf");
        holliday.setTypeface(custom_fonts);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(MainActivity.this, SignUp.class);
                startActivity(it);
            }
        });

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (TextView) findViewById(R.id.getstarted);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user, pass;
                user = username.getText().toString().trim();
                pass = password.getText().toString().trim();
                if (user.equals("") || pass.equals("")) {
                    Toast.makeText(getApplicationContext(), "Username or password is empty", Toast.LENGTH_LONG).show();
                } else {
                    csprogress = new ProgressDialog(MainActivity.this);
                    csprogress.show();
                    csprogress.setCancelable(false);
                    csprogress.setMessage("Please wait...");
                    ParseUser.logInInBackground(user, pass, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            csprogress.dismiss();
                            if (user != null) {
                                Intent it = new Intent(MainActivity.this, LoggedIn.class);
                                startActivity(it);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        welcomeScreen.onSaveInstanceState(outState);
    }

    private class Login extends AsyncTask<Void, Void, Void> {

        Boolean result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            csprogress = new ProgressDialog(MainActivity.this);
            csprogress.show();
            csprogress.setCancelable(false);
            csprogress.setMessage("Please wait...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
            result = connectionDetector.isConnectingToInternet();
            if (result) {
                ParseUser parseUser = ParseUser.getCurrentUser();
                if (parseUser != null) {
                    Intent it = new Intent(MainActivity.this, LoggedIn.class);
                    startActivity(it);
                    finish();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            csprogress.dismiss();
            if (!result) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Internet Connection Error")
                        .setCancelable(false)
                        .setMessage("It seems as if you are not connected to internet")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Login().execute();
                            }
                        })
                        .show();
            }
        }
    }
}
