package gbabazaar.gbabazaar;

import com.stephentuso.welcome.WelcomeScreenBuilder;
import com.stephentuso.welcome.ui.WelcomeActivity;
import com.stephentuso.welcome.util.WelcomeScreenConfiguration;

/**
 * Created by harsh on 26-Jul-16.
 */
public class MyWelcomeActivity extends WelcomeActivity {
    @Override
    protected WelcomeScreenConfiguration configuration() {
        return new WelcomeScreenBuilder(this)
                .theme(R.style.WelcomeScreenTheme_Light)
                .titlePage(R.drawable.iconnew, "GBA Bazaar", R.color.red_background, true)
                .basicPage(R.drawable.category, "Many categories to choose from", "We have brought several categories to make it straight and simple. And we are striving hard to make it even better.", R.color.blue_background)
                .basicPage(R.drawable.click, "Post Ad in just a few clicks", "Post your ad and it will reach millions in just a few clicks. Selling products haven't been this easy .", R.color.purple_background)
                .basicPage(R.drawable.free, "And its totally free...", "All it costs is just a few seconds to post the ad. Its totally free and will always be.", R.color.teal_background)
                .swipeToDismiss(true)
                .build();
    }
}
