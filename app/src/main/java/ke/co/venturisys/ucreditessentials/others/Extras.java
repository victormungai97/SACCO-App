package ke.co.venturisys.ucreditessentials.others;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ke.co.venturisys.ucreditessentials.R;

import static ke.co.venturisys.ucreditessentials.others.Constants.FILE;
import static ke.co.venturisys.ucreditessentials.others.Constants.RES_ID;
import static ke.co.venturisys.ucreditessentials.others.Constants.URI;
import static ke.co.venturisys.ucreditessentials.others.Constants.URL;

public class Extras {

    /*
     * Method used to set up action bar for activities
     */
    public static void setUpActionBar(String title, AppCompatActivity activity) {
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(
                    Html.fromHtml("<font color='#fff'>" + title
                            + "</font>"));
            final Drawable upArrow = activity.getResources().getDrawable(R.drawable.ic_arrow_left);
            upArrow.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }

    /*
     * This method provides for changing of fragments based on choice selected
     * either on navigation menu or within the fragments
     */
    public static void changeFragment(final Fragment homeFragment, Handler mHandler,
                                      final String CURRENT_TAG, final AppCompatActivity activity) {
        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                // result in 'shaking' appearance when the fragments transition
//                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
//                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, homeFragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        mHandler.post(mPendingRunnable);
    }

    /*
     * Method redirects to target activity while popping every other activity off stack
     */
    public static void exitToTargetActivity(AppCompatActivity activity, Class class_) {
        Intent intent = new Intent(activity, class_);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish(); // close this activity
    }

    /*
     * Check if length of edit text page is equal to 0, return true if it is
     */
    public static boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    /*
     * Method to successfully load image to given image view with right scale
     */
    public static void loadPictureToImageView(@NonNull HashMap<String, Object> source,
                                              int placeholder,
                                              @NonNull ImageView imageView,
                                              boolean toTransform, /* checks whether to make image view circular eg for profile image in slide menu*/
                                              boolean fit,
                                              boolean centerInside,
                                              boolean border) {

        Picasso picasso;
        RequestCreator creator = null;

        try {

            picasso = Picasso.get();

            // initialise creator from various sources
            if (source.containsKey(URL)) creator = picasso.load((String) source.get(URL));
            if (source.containsKey(RES_ID)) creator = picasso.load((int) source.get(RES_ID));
            if (source.containsKey(FILE)) creator = picasso.load((File) source.get(FILE));
            if (source.containsKey(URI)) creator = picasso.load((Uri) source.get(URI));

            if (creator != null) {
                // set up creator with various scenarios
                creator = creator.placeholder(placeholder)
                        .error(android.R.drawable.ic_delete)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE);
                if (toTransform) creator = creator.transform(new CircleTransform(border));
                if (fit) creator = creator.fit();
                if (centerInside) creator = creator.centerInside();

                // load into image view considering the possible results of loading
                creator.into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Picasso Success", "Successful image loading");
                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e("Picasso Error", "Unsuccessful image loading");
                        exception.printStackTrace();
                    }
                });
            }

        } catch (Exception ex) {
            Log.e("PICTURES ERROR", "Something went wrong");
            ex.printStackTrace();
        }
    }

    /*
     * Set badge on the menu item from our Activity.
     */
    public static void setBadgeCount(Context context, LayerDrawable icon, String count, int res) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(res);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(res, badge);
    }

    /**
     * Method is used for checking valid email id format.
     *
     * @param email Address being entered
     * @return boolean true for valid, false for invalid
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /*
     * This method creates a snack bar in fragments that require internet
     * so that providing an interface for the user to enable internet connection
     */
    public static void requestInternetAccess(@NonNull final Activity context) {
        if (!isNetworkAvailable(context)) {
            Toast.makeText(context, "Please enable internet connection", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method checks whether there is internet connectivity
     * It calls the getActiveNetworkInfo and then checks if returns null, which it then returns
     * as true for network connection or false if otherwise
     *
     * @param context Current context
     * @return State of net connection
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
