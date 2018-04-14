package ke.co.venturisys.ucreditessentials.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import ke.co.venturisys.ucreditessentials.R;
import ke.co.venturisys.ucreditessentials.others.GeneralDialogFragment;

import static ke.co.venturisys.ucreditessentials.others.Constants.ARG_EMAIL;
import static ke.co.venturisys.ucreditessentials.others.Constants.ARG_NAME;
import static ke.co.venturisys.ucreditessentials.others.Constants.ARG_PHONE_NUMBER;

public class SaccoDialogFragment extends GeneralDialogFragment {

    String name, email, phone;

    public SaccoDialogFragment() {
        super.alertDialogLayout = R.layout.dialog_add_sacco;
    }

    public static SaccoDialogFragment newInstance(String name, String email, String phone) {

        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_PHONE_NUMBER, phone);

        SaccoDialogFragment fragment = new SaccoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        if (args != null) {
            name = args.getString(ARG_NAME);
            email = args.getString(ARG_EMAIL);
            phone = args.getString(ARG_PHONE_NUMBER);
        }
    }

    @Override
    protected void initializeWidgets(View view) {
        setAlertDialogTitle(getString(R.string.add_sacco));
        Button yesButton = view.findViewById(R.id.verify_send_button);
        Button noButton = view.findViewById(R.id.verify_cancel_button);
    }
}
