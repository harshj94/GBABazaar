package gbabazaar.gbabazaar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import custom_font.MyTextView;

public class MainActivity extends AppCompatActivity {
    TextView holliday;
    EditText username, password;
    TextView login;
    MyTextView create;
    ProgressDialog csprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Login().execute();

        create = (MyTextView) findViewById(R.id.create);
        holliday = (TextView) findViewById(R.id.holliday);

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

    private class Login extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            ParseUser parseUser = ParseUser.getCurrentUser();
            if (parseUser != null) {
                Intent it = new Intent(MainActivity.this, LoggedIn.class);
                startActivity(it);
                finish();
            }
            return null;
        }
    }
}
