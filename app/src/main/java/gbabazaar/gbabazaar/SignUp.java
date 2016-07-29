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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import custom_font.MyTextView;

public class SignUp extends AppCompatActivity {
    TextView holliday;
    MyTextView signin;
    EditText name, emailid, phone, password, confirmpass;
    TextView signup,needhelp;
    ParseUser parseUser;
    ProgressDialog csprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        signin = (MyTextView) findViewById(R.id.signin);
        holliday = (TextView) findViewById(R.id.holliday);
        needhelp=(TextView)findViewById(R.id.needhelp);

        Typeface custom_fonts = Typeface.createFromAsset(getAssets(), "fonts/ArgonPERSONAL-Regular.otf");
        holliday.setTypeface(custom_fonts);

        new NetCheck().execute();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        name = (EditText) findViewById(R.id.name);
        emailid = (EditText) findViewById(R.id.emailid);
        phone = (EditText) findViewById(R.id.mobile);
        password = (EditText) findViewById(R.id.password);
        confirmpass = (EditText) findViewById(R.id.confirmpass);
        signup = (TextView) findViewById(R.id.getstarted);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String namee, mail, mobile, pass, cpass;

                namee = name.getText().toString().trim();
                mail = emailid.getText().toString();
                mobile = phone.getText().toString();
                pass = password.getText().toString();
                cpass = confirmpass.getText().toString();

                mail = mail.trim();
                mobile = mobile.trim();
                pass = pass.trim();
                cpass = cpass.trim();

                if (namee.equals("") || mobile.equals("") || pass.equals("") || cpass.equals("")) {
                    Toast.makeText(getApplicationContext(), "Compulsory fields cannot be left blank!!!", Toast.LENGTH_LONG).show();
                } else if (!pass.equals(cpass)) {
                    Toast.makeText(getApplicationContext(), "Password and Confirm Password do no match!!!", Toast.LENGTH_LONG).show();
                } else {
                    parseUser = new ParseUser();
                    parseUser.setUsername(mobile);
                    parseUser.setPassword(pass);
                    parseUser.put("Name", namee);
                    if (!mail.equals("")) {
                        parseUser.setEmail(mail);
                    }
                    csprogress = new ProgressDialog(SignUp.this);
                    csprogress.show();
                    csprogress.setCancelable(false);
                    csprogress.setMessage("Please wait...");
                    parseUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            csprogress.dismiss();
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "You have been successfully registered.", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        needhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SignUp.this)
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
    }

    private class NetCheck extends AsyncTask<Void, Void, Void> {
        Boolean result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            csprogress = new ProgressDialog(SignUp.this);
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
                new AlertDialog.Builder(SignUp.this)
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
