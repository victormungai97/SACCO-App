package ke.co.venturisys.ucreditessentials.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import ke.co.venturisys.ucreditessentials.R;
import ke.co.venturisys.ucreditessentials.activities.MainActivity;

import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_REGISTER;
import static ke.co.venturisys.ucreditessentials.others.Extras.changeFragment;
import static ke.co.venturisys.ucreditessentials.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.ucreditessentials.others.Extras.isEmpty;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    View view;
    EditText emailOrName, password;
    Button loginBtn;
    TextView forgotPassword, createAcct;
    ImageView facebookBtn, googleBtn;

    public LoginFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate layout
        view = inflater.inflate(R.layout.fragment_login, container, false);
        assert getActivity() != null;

        // initialise widgets
        emailOrName = view.findViewById(R.id.email_or_name);
        password = view.findViewById(R.id.password);
        loginBtn = view.findViewById(R.id.login_button);
        forgotPassword = view.findViewById(R.id.forgot_password);
        createAcct = view.findViewById(R.id.create_redirect);
        facebookBtn = view.findViewById(R.id.facebook_btn);
        googleBtn = view.findViewById(R.id.google_plus_btn);

        // enter app if successful and show warning if not
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitLoginForm();
            }
        });

        // redirect to redirection
        createAcct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new RegisterFragment(), new Handler(), TAG_REGISTER,
                        (AppCompatActivity) getActivity());
            }
        });

        return view;
    }

    private void submitLoginForm() {
        if (isEmpty(emailOrName)) emailOrName.setError("User name or email required");
        else if (isEmpty(password)) password.setError("Password required");
        else exitToTargetActivity((AppCompatActivity) getActivity(), MainActivity.class);
    }

}
