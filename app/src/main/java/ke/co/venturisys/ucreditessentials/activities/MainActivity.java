package ke.co.venturisys.ucreditessentials.activities;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nonnull;

import ke.co.ke.venturisys.ucreditessentials.FindCustomerShortenedQuery;
import ke.co.venturisys.ucreditessentials.R;
import ke.co.venturisys.ucreditessentials.fragments.BalanceStatementFragment;
import ke.co.venturisys.ucreditessentials.fragments.BillPaymentsFragment;
import ke.co.venturisys.ucreditessentials.fragments.FixedDepositsFragment;
import ke.co.venturisys.ucreditessentials.fragments.ForexFragment;
import ke.co.venturisys.ucreditessentials.fragments.InterAccountTransferFragment;
import ke.co.venturisys.ucreditessentials.fragments.LoanApplicationFragment;
import ke.co.venturisys.ucreditessentials.fragments.NotificationsFragment;
import ke.co.venturisys.ucreditessentials.fragments.ProfileFragment;
import ke.co.venturisys.ucreditessentials.fragments.SaccoLocationFragment;
import ke.co.venturisys.ucreditessentials.fragments.SettingsFragment;
import ke.co.venturisys.ucreditessentials.fragments.StandingOrdersFragment;
import ke.co.venturisys.ucreditessentials.fragments.ViewSaccosFragment;
import ke.co.venturisys.ucreditessentials.others.MyApolloClient;

import static ke.co.venturisys.ucreditessentials.others.Constants.ERROR;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_ACCOUNT_TRANSFER;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_BALANCE_STATEMENT;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_BILL_PAYMENTS;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_FIXED_DEPOSIT;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_FOREIGN_EXCHANGE;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_LOAN_APPLICATION;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_NOTIFICATIONS;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_PROFILE;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_SACCO_LIST;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_SACCO_LOCATIONS;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_SETTINGS;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_STANDING_ORDERS;
import static ke.co.venturisys.ucreditessentials.others.Constants.URL;
import static ke.co.venturisys.ucreditessentials.others.Extras.changeFragment;
import static ke.co.venturisys.ucreditessentials.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.ucreditessentials.others.Extras.isNetworkAvailable;
import static ke.co.venturisys.ucreditessentials.others.Extras.loadPictureToImageView;
import static ke.co.venturisys.ucreditessentials.others.Extras.requestInternetAccess;
import static ke.co.venturisys.ucreditessentials.others.Extras.setBadgeCount;
import static ke.co.venturisys.ucreditessentials.others.URLs.urlProfileImg;

public class MainActivity extends AppCompatActivity {

    // tag for fragment to be shown
    public static String CURRENT_TAG = TAG_BALANCE_STATEMENT;
    // index to identify current nav menu item
    public static int navItemIndex = 0;
    Handler mHandler;
    // flag to load home fragment when user presses back key
    boolean shouldLoadHomeFragOnBackPress = true;

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    View navHeader;
    ActionBarDrawerToggle actionBarDrawerToggle;
    LinearLayout headerLayout;
    TextView tvName, tvDate;
    ImageView imgProfile;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authListener;
    FirebaseUser user;

    public static void setCurrentTag(String currentTag) {
        CURRENT_TAG = currentTag;
    }

    public static void setNavItemIndex(int navItemIndex) {
        MainActivity.navItemIndex = navItemIndex;
    }

    /**
     * Method returns current time in specified format
     */
    public static String getCurrentDate() {
        // get current time and set as file name
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
        Calendar calendar = Calendar.getInstance();
        return df.format(calendar.getTime());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        // Get Current user
        user = auth.getCurrentUser();
        // check if user has been logged out and leave page is true
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    exitToTargetActivity(MainActivity.this, LoginActivity.class);
                }
            }
        };

        mHandler = new Handler();

        // initialise widgets
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        // navigation view header
        navHeader = navigationView.getHeaderView(0);
        tvName = navHeader.findViewById(R.id.nameTextView);
        tvDate = navHeader.findViewById(R.id.dateTextView);
        imgProfile = navHeader.findViewById(R.id.img_profile);
        headerLayout = navHeader.findViewById(R.id.navHeaderLayout);

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navItemIndex = 11;
                CURRENT_TAG = TAG_PROFILE;
                changeFragment(new ProfileFragment(), mHandler, CURRENT_TAG, MainActivity.this);
            }
        });

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navItemIndex = 11;
                CURRENT_TAG = TAG_PROFILE;
                changeFragment(new ProfileFragment(), mHandler, CURRENT_TAG, MainActivity.this);
            }
        });

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_BALANCE_STATEMENT;
            loadHomeFragment();
        }

    }

    /*
     * Loads the navigation drawer header information
     * like profile image, name, location and notifications action dot
     */
    private void loadNavHeader() {
        tvName.setText(getString(R.string.name_placeholder));
        tvDate.setText(getCurrentDate());

        // carry out a GraphQL query to get the user's name, location and profile image
        // please ensure that the user is signed in first and there is internet connection
        if (user != null) {
            if (isNetworkAvailable(this)) {
                MyApolloClient.getMyApolloClient().query(
                        FindCustomerShortenedQuery.builder().email(Objects.requireNonNull(user.getEmail())).build()
                ).enqueue(new ApolloCall.Callback<FindCustomerShortenedQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull final Response<FindCustomerShortenedQuery.Data> response) {
                        // update UI on the activity's UI thread to reflect query
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update text views
                                tvName.setText(Objects.requireNonNull(Objects.requireNonNull
                                        (response.data()).Customer()).name());

                                // load profile image
                                String photo_url = Objects.requireNonNull(Objects.requireNonNull(response.data()).Customer()).picture();
                                HashMap<String, Object> src = new HashMap<>();
                                src.put(URL, photo_url);
                                loadPictureToImageView(src, R.drawable.avatar, imgProfile, true, false,
                                        false, false);
                            }
                        });
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {
                        // Log error so as to fix it
                        Log.e(ERROR, "onFailure: Something went wrong. " + e.getMessage());
                    }
                });
            } else requestInternetAccess(this);
        }

        // if nav header is selected
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        // if user clicks on profile image, blow it up to full scale
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ProfileImageActivity.newIntent(MainActivity.this, urlProfileImg);
                startActivity(intent);
            }
        });

        // set count of notifications on bell
        int notifications = 1;
        // LayerDrawable is a Drawable that manages an array of other Drawables
        LayerDrawable icon = (LayerDrawable) navigationView.getMenu().
                findItem(R.id.nav_notifications).getIcon();
        setBadgeCount(this, icon, String.valueOf(notifications), R.id.ic_notifications_badge);
    }

    /*
     * Initializes the Navigation Drawer by creating necessary click listeners
     * and other functions.
     */
    private void setUpNavigationView() {


        //Setting Navigation View Item Selected Listener
        // to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView
                .OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                // Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    // Replace the current tag value with selected fragment tag value
                    case R.id.nav_balance_statement:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_BALANCE_STATEMENT;
                        break;
                    case R.id.nav_loan_application:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_LOAN_APPLICATION;
                        break;
                    case R.id.nav_standing_orders:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_STANDING_ORDERS;
                        break;
                    case R.id.nav_bill_payments:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_BILL_PAYMENTS;
                        break;
                    case R.id.nav_fixed_deposit:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_FIXED_DEPOSIT;
                        break;
                    case R.id.nav_inter_account_transfer:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_ACCOUNT_TRANSFER;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_forex:
                        navItemIndex = 7;
                        CURRENT_TAG = TAG_FOREIGN_EXCHANGE;
                        break;
                    case R.id.nav_sacco_locations:
                        navItemIndex = 8;
                        CURRENT_TAG = TAG_SACCO_LOCATIONS;
                        break;
                    case R.id.nav_view_saccos:
                        navItemIndex = 9;
                        CURRENT_TAG = TAG_SACCO_LIST;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 10;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_profile:
                        navItemIndex = 11;
                        CURRENT_TAG = TAG_PROFILE;
                        break;
                    case R.id.nav_logout:
                        signOut();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                // Checking if the item is in checked state or not and invert the state
                // if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open
                super.onDrawerOpened(drawerView);
            }
        };

        // Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // calling sync state is necessary for hamburger icon to show up
        actionBarDrawerToggle.syncState();
    }

    //sign out method
    public void signOut() {
        Toast.makeText(this, "Come back soon!", Toast.LENGTH_SHORT).show();
        auth.signOut();
        exitToTargetActivity(this, LoginActivity.class);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_BALANCE_STATEMENT;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    /*
     *  Loads the fragment returned from getHomeFragment() function into FrameLayout.
     *  It also takes care of other things like changing the toolbar activity_title,
     *  hiding / showing fab and
     *  invalidating the options menu so that new menu can be loaded for different fragment.
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawerLayout.closeDrawers();
            return;
        }

        changeFragment(getHomeFragment(), mHandler, CURRENT_TAG, this);

        // Closing drawer on item click
        drawerLayout.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    /*
     * Returns the appropriate Fragment depending on the nav menu item user selected
     */
    private Fragment getHomeFragment() {

        switch (navItemIndex) {
            case 0:
                // balance and statements
                return new BalanceStatementFragment();
            case 1:
                // loan application
                return new LoanApplicationFragment();
            case 2:
                // standing orders
                return new StandingOrdersFragment();
            case 3:
                // bill payments
                return new BillPaymentsFragment();
            case 4:
                // fixed deposits
                return new FixedDepositsFragment();
            case 5:
                // inter account transfer
                return new InterAccountTransferFragment();
            case 6:
                // notifications
                return new NotificationsFragment();
            case 7:
                // forex
                return new ForexFragment();
            case 8:
                // sacco locations
                return new SaccoLocationFragment();
            case 9:
                // list of available saccos
                return new ViewSaccosFragment();
            case 10:
                // settings
                return new SettingsFragment();
            case 11:
                // profile
                return new ProfileFragment();
            default:
                return new BalanceStatementFragment();
        }

    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

}
