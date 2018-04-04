package ke.co.venturisys.ucreditessentials.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import ke.co.venturisys.ucreditessentials.R;

import static ke.co.venturisys.ucreditessentials.others.Extras.setUpActionBar;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    // activity_title of toolbar
    String title;

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        // show title bar
        if (!showTitle()) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
        // host fragment in this activity
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.frame);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.frame, fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // set up toolbar
        setUpActionBar(title, this);
    }

    protected abstract Fragment createFragment();

    protected abstract boolean showTitle();

}
