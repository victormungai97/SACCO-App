package ke.co.venturisys.ucreditessentials.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ke.co.venturisys.ucreditessentials.R;

import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_LOGIN;
import static ke.co.venturisys.ucreditessentials.others.Extras.changeFragment;
import static ke.co.venturisys.ucreditessentials.others.Extras.isEmailValid;
import static ke.co.venturisys.ucreditessentials.others.Extras.isEmpty;
import static ke.co.venturisys.ucreditessentials.others.Extras.isNetworkAvailable;
import static ke.co.venturisys.ucreditessentials.others.Extras.requestInternetAccess;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordFragment extends Fragment {

    View view;
    CoordinatorLayout coordinatorLayout;
    EditText emailAddress;
    Button btnResetPassword;
    Button btnBack;
    FirebaseAuth auth;

    public ResetPasswordFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        assert getActivity() != null;
        auth = FirebaseAuth.getInstance();

        // initialise widgets
        coordinatorLayout = view.findViewById(R.id.reset_parent_layout);
        emailAddress = view.findViewById(R.id.input_email);
        btnResetPassword = view.findViewById(R.id.btn_reset_password);
        btnBack = view.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new LoginFragment(), new Handler(), TAG_LOGIN,
                        (AppCompatActivity) getActivity());
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitResetForm();
            }
        });

        return view;
    }

    private void submitResetForm() {
        assert getActivity() != null;
        if (isNetworkAvailable(getActivity())) {
            if (isEmpty(emailAddress)) emailAddress.setError("Email address required");
            else if (!isEmailValid(emailAddress.getText().toString()))
                emailAddress.setError("Invalid email entered");

            auth.sendPasswordResetEmail(emailAddress.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Password reset successful. Check email for further instructions",
                                        Toast.LENGTH_SHORT).show();
                                changeFragment(new LoginFragment(), new Handler(), TAG_LOGIN,
                                        (AppCompatActivity) getActivity());
                            } else {
                                Toast.makeText(getContext(), "Password reset failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } else requestInternetAccess(getActivity());
    }

}
