package gbabazaar.gbabazaar;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
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
    TextView signup;
    ParseUser parseUser;
    ProgressDialog csprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        signin = (MyTextView) findViewById(R.id.signin);
        holliday = (TextView) findViewById(R.id.holliday);

        Typeface custom_fonts = Typeface.createFromAsset(getAssets(), "fonts/ArgonPERSONAL-Regular.otf");
        holliday.setTypeface(custom_fonts);

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
    }
}
