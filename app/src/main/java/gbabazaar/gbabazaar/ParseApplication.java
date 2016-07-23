package gbabazaar.gbabazaar;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by harsh on 29-Jun-16.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("6md5fSIFNrcpXc0ZStnJos7SW4Rnrok0Hyu7Uoes")
                .clientKey("JkJobVzYq3d6qHSZlVD6I1Whgo0rirOS8lEcZyba")
                .server("https://parseapi.back4app.com/")
                .enableLocalDataStore()
                .build());
    }
}
