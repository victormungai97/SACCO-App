package ke.co.venturisys.ucreditessentials.activities;

import android.support.v4.app.Fragment;

import ke.co.venturisys.ucreditessentials.fragments.LoginFragment;

public class LoginActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        setTitle("");
        return new LoginFragment();
    }

    @Override
    protected boolean showTitle() {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
