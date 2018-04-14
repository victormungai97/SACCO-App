package ke.co.venturisys.ucreditessentials.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import ke.co.venturisys.ucreditessentials.R;
import ke.co.venturisys.ucreditessentials.activities.MainActivity;

import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_REGISTER;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_RESET_PASSWORD;
import static ke.co.venturisys.ucreditessentials.others.Extras.changeFragment;
import static ke.co.venturisys.ucreditessentials.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.ucreditessentials.others.Extras.isEmpty;
import static ke.co.venturisys.ucreditessentials.others.Extras.isNetworkAvailable;
import static ke.co.venturisys.ucreditessentials.others.Extras.requestInternetAccess;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    View view;
    EditText emailOrName, password;
    Button loginBtn;
    TextView forgotPassword, createAcct;
    ImageView facebookBtn, googleBtn;
    FirebaseAuth auth; // Firebase facilitates easy logging in using email or google or facebook

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get instance of Firebase auth
        auth = FirebaseAuth.getInstance();
        // there is a user already logged in so just go to main activity
        if (auth.getCurrentUser() != null) {
            exitToTargetActivity((AppCompatActivity) getActivity(), MainActivity.class);
        }

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

        // redirect to create account fragment
        createAcct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new RegisterFragment(), new Handler(), TAG_REGISTER,
                        (AppCompatActivity) getActivity());
            }
        });

        // redirect to password reset
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new ResetPasswordFragment(), new Handler(), TAG_RESET_PASSWORD,
                        (AppCompatActivity) getActivity());
            }
        });

        return view;
    }

    private void submitLoginForm() {
        assert getActivity() != null; // this prevents a scenario where the fragment is unattached to activity
        if (isNetworkAvailable(getActivity())) {
            String email = emailOrName.getText().toString().trim(),
                    pass = password.getText().toString().trim();
            if (isEmpty(emailOrName)) emailOrName.setError("User name or email required");
            else if (isEmpty(password)) password.setError("Password required");
            else if (pass.length() < 6)
                password.setError("Password too short. At least 6 characters required");
            else {
                Toast.makeText(getActivity(), "Authenticating...", Toast.LENGTH_SHORT).show();
                // authenticate user
                auth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(getActivity(), "Authentication processed", Toast.LENGTH_SHORT).show();
                                // if authentication failed, show error
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Authentication failed",
                                            Toast.LENGTH_SHORT).show();
                                    try {
                                        if (Objects.requireNonNull(task.getException()).toString().contains("InvalidCredentialsException"))
                                            password.setError("Invalid password");
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    try {
                                        if (Objects.requireNonNull(task.getException()).toString().contains("InvalidUserException"))
                                            emailOrName.setError("Email address not registered");
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    Log.e("ERROR", "Error message" + task.getException());

                                } else {
                                    // direct to main activity
                                    Toast.makeText(getActivity(), "Welcome", Toast.LENGTH_SHORT).show();
                                    exitToTargetActivity((AppCompatActivity) getActivity(), MainActivity.class);
                                }
                            }
                        });
            }
        } else requestInternetAccess(getActivity());
    }

}
