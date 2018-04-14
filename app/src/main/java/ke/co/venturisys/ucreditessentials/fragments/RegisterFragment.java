package ke.co.venturisys.ucreditessentials.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import ke.co.ke.venturisys.ucreditessentials.AddCustomerMutation;
import ke.co.ke.venturisys.ucreditessentials.AllSaccosNameQuery;
import ke.co.venturisys.ucreditessentials.R;
import ke.co.venturisys.ucreditessentials.activities.MainActivity;
import ke.co.venturisys.ucreditessentials.others.MyApolloClient;

import static ke.co.venturisys.ucreditessentials.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.ucreditessentials.others.Extras.isEmailValid;
import static ke.co.venturisys.ucreditessentials.others.Extras.isEmpty;
import static ke.co.venturisys.ucreditessentials.others.Extras.isNetworkAvailable;
import static ke.co.venturisys.ucreditessentials.others.Extras.requestInternetAccess;

/**
 * A simple {@link Fragment} subclass.
 * Fragments provide for greater UI flexibility hence preferred over activities
 */
public class RegisterFragment extends Fragment {

    View view;
    ImageView facebookBtn, googleBtn;
    TextInputLayout phoneInputLayout;
    IntlPhoneInput phoneInputView;
    Spinner spinner;
    String phoneNumber;
    EditText userName, emailAddress, passWord;
    Button createAccount;
    FirebaseAuth auth;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get firebase instance
        auth = FirebaseAuth.getInstance();

        // inflate layout
        view = inflater.inflate(R.layout.fragment_register, container, false);
        assert getActivity() != null;

        facebookBtn = view.findViewById(R.id.facebook_btn);
        googleBtn = view.findViewById(R.id.google_plus_btn);
        phoneInputView = view.findViewById(R.id.input_phone_number);
        phoneInputLayout = view.findViewById(R.id.input_layout_phone_number);
        userName = view.findViewById(R.id.user_name);
        spinner = view.findViewById(R.id.saccos_list);
        emailAddress = view.findViewById(R.id.email_address);
        passWord = view.findViewById(R.id.password);
        createAccount = view.findViewById(R.id.create_button);

        // get list of registered saccos
        MyApolloClient.getMyApolloClient().query(
                AllSaccosNameQuery.builder().build()
        ).enqueue(new ApolloCall.Callback<AllSaccosNameQuery.Data>() {
            @Override
            public void onResponse(@Nonnull final Response<AllSaccosNameQuery.Data> response) {
                // run on activity's UI thread
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // update list in spinner
                        List<String> list = new ArrayList<>();
                        List<AllSaccosNameQuery.AllSaccose> responses = Objects.requireNonNull
                                (response.data()).allSaccoses();
                        for (int i = 0; i < responses.size(); i++ ) {
                            list.add(responses.get(i).name());
                        }
                        ArrayAdapter<String > adapter = new ArrayAdapter<>(
                                getActivity(), android.R.layout.simple_spinner_item, list
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    }
                });
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {

            }
        });

        // get country of user and set to default using SIM card network
        assert getActivity() != null;
        TelephonyManager tm = (TelephonyManager)
                getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        assert tm != null;
        String countryCode = tm.getSimCountryIso();
        phoneInputView.setEmptyDefault(countryCode);

        phoneInputView.setOnValidityChange(new IntlPhoneInput.IntlPhoneInputListener() {
            @Override
            public void done(View view, boolean isValid) {
                phoneNumber = phoneInputView.getNumber();
            }
        });

        // authenticate account creation
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRegistrationForm();
            }
        });

        return view;
    }

    private void submitRegistrationForm() {
        // prevent situation where fragment is unattached
        assert getActivity() != null;
        phoneNumber = phoneInputView.getNumber();
        if (isNetworkAvailable(getActivity())) {
            String pass = passWord.getText().toString().trim(),
                    email = emailAddress.getText().toString().trim();
            if (isEmpty(userName)) userName.setError("User name required");
            else if (isEmpty(emailAddress)) emailAddress.setError("Email address required");
            else if (!isEmailValid(emailAddress.getText().toString()))
                emailAddress.setError("Invalid email entered");
            else if (phoneNumber.length() == 0 || phoneNumber.equals(""))
                Toast.makeText(getActivity(), "Phone number required", Toast.LENGTH_SHORT).show();
            else if (phoneNumber.length() != 13 || !phoneInputView.isValid())
                Toast.makeText(getActivity(), "Invalid phone number", Toast.LENGTH_SHORT).show();
            else if (isEmpty(passWord)) passWord.setError("Password required");
            else if (pass.length() < 6)
                passWord.setError("Password too short. At least 6 characters required");
            else {
                Toast.makeText(getActivity(), "Creating account...", Toast.LENGTH_SHORT).show();
                // create account
                auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(getActivity(), "Process complete", Toast.LENGTH_SHORT).show();
                                Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());
                                if (!task.isSuccessful()) {
                                    // if creation failed
                                    Toast.makeText(getActivity(), "Account creation failed",
                                            Toast.LENGTH_SHORT).show();
                                    Log.e("ERROR", "Error message" + task.getException());
                                } else {
                                    // register user by mutating(post) user's info to GraphQL server
                                    MyApolloClient.getMyApolloClient().mutate(
                                            AddCustomerMutation.builder()
                                                    .name(userName.getText().toString().trim())
                                                    .email(emailAddress.getText().toString().trim())
                                                    .phone(phoneInputView.getNumber())
                                                    .sacco(String.valueOf(spinner.getSelectedItem())).build()
                                    ).enqueue(new ApolloCall.Callback<AddCustomerMutation.Data>() {
                                        @Override
                                        public void onResponse(@Nonnull Response<AddCustomerMutation.Data> response) {
                                            exitToTargetActivity((AppCompatActivity) getActivity(), MainActivity.class);
                                        }

                                        @Override
                                        public void onFailure(@Nonnull ApolloException e) {
                                            Toast.makeText(getActivity(), "Account creation failed",
                                                    Toast.LENGTH_SHORT).show();
                                            Log.e("ERROR", "Error message" + e.getMessage());
                                        }
                                    });
                                }
                            }
                        });
            }
        } else requestInternetAccess(getActivity());
    }

}
