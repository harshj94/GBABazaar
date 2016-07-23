package gbabazaar.gbabazaar;

import android.app.ProgressDialog;
import android.os.Bundle;
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
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
