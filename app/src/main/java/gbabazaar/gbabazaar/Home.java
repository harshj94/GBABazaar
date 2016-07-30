package gbabazaar.gbabazaar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.stephentuso.welcome.WelcomeScreenHelper;

public class Home extends AppCompatActivity {

    WelcomeScreenHelper welcomeScreen;
    CardView agriculture, fruits, vegetables, home, automobiles, hotels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        welcomeScreen = new WelcomeScreenHelper(this, MyWelcomeActivity.class);
        welcomeScreen.show(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, AdUpload.class));
            }
        });

        agriculture = (CardView) findViewById(R.id.agriculture);
        fruits = (CardView) findViewById(R.id.fruits);
        vegetables = (CardView) findViewById(R.id.vegetables);
        home = (CardView) findViewById(R.id.home);
        automobiles = (CardView) findViewById(R.id.automobiles);
        hotels = (CardView) findViewById(R.id.hotel);

        agriculture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewIntent("Agriculture");
            }
        });

        fruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewIntent("Fruits");
            }
        });

        vegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewIntent("Vegetables");
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewIntent("Home");
            }
        });

        automobiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewIntent("Automobiles");
            }
        });

        hotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewIntent("Hotels");
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        welcomeScreen.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_myaccount) {
            Intent it = new Intent(Home.this, MainActivity.class);
            startActivity(it);
            return true;
        }

        if (id == R.id.action_aboutus) {
            new AlertDialog.Builder(Home.this)
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

        if (id == R.id.action_aboutDeveloper) {
            new AlertDialog.Builder(Home.this)
                    .setTitle("Developer")
                    .setMessage("Email:\tharshkumarjain1994@gmail.com\nMobile:\t+91 9168004402")
                    .setNegativeButton("Call Us", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:+919168004402"));
                            startActivity(intent);
                        }
                    })
                    .setNeutralButton("Email Us", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "harshkumarjain1994@gmail.com", null));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                            startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        }
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    void openNewIntent(String category) {
        Intent it = new Intent(Home.this, LoggedIn.class);
        it.putExtra("category", category);
        startActivity(it);
    }
}
