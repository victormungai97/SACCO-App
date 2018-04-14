package ke.co.venturisys.ucreditessentials.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import javax.annotation.Nonnull;

import ke.co.ke.venturisys.ucreditessentials.AddCustomerMutation;
import ke.co.venturisys.ucreditessentials.R;
import ke.co.venturisys.ucreditessentials.activities.MainActivity;
import ke.co.venturisys.ucreditessentials.others.MyApolloClient;

import static ke.co.venturisys.ucreditessentials.others.Constants.ARG_EMAIL;
import static ke.co.venturisys.ucreditessentials.others.Constants.ARG_NAME;
import static ke.co.venturisys.ucreditessentials.others.Constants.ARG_PHONE_NUMBER;
import static ke.co.venturisys.ucreditessentials.others.Constants.ERROR;
import static ke.co.venturisys.ucreditessentials.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.ucreditessentials.others.Extras.isEmpty;
import static ke.co.venturisys.ucreditessentials.others.Extras.isNetworkAvailable;
import static ke.co.venturisys.ucreditessentials.others.Extras.requestInternetAccess;

public class EditProfileFragment extends Fragment {

    View view;
    RelativeLayout nameLayout, emailLayout;
    TextInputLayout phoneInputLayout, emailInputLayout;
    IntlPhoneInput phoneInputView;
    EditText editName, editEmail;
    Button submitButton;
    String myInternationalNumber = "";
    String name, email;

    public static EditProfileFragment newInstance(
            String name, String email, String myInternationalNumber) {

        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_PHONE_NUMBER, myInternationalNumber);

        EditProfileFragment fragment = new EditProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        assert getActivity() != null;

        // retrieve arguments passed to fragment
        if (bundle != null) {
            name = bundle.getString(ARG_NAME);
            email = bundle.getString(ARG_EMAIL);
            myInternationalNumber = bundle.getString(ARG_PHONE_NUMBER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_profile, container, false);
        assert getActivity() != null;

        // initialise widgets
        nameLayout = view.findViewById(R.id.name_relative_layout);
        emailLayout = view.findViewById(R.id.email_relative_layout);
        phoneInputView = view.findViewById(R.id.input_phone_number);
        phoneInputLayout = view.findViewById(R.id.input_layout_phone_number);
        emailInputLayout = view.findViewById(R.id.input_layout_email);
        editName = view.findViewById(R.id.input_name);
        editEmail = view.findViewById(R.id.input_email);
        submitButton = view.findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRegistrationForm();
            }
        });

        // set values to edit text if there are any
        editName.setText(name);
        editEmail.setText(email);
        phoneInputView.setNumber(myInternationalNumber);

        // make email uneditable
        editEmail.setEnabled(false);
        editEmail.setBackgroundColor(Color.LTGRAY);
        emailInputLayout.setBackgroundColor(Color.LTGRAY);
        emailLayout.setBackgroundColor(Color.LTGRAY);
        editEmail.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));

        assert getActivity() != null;
        TelephonyManager tm = (TelephonyManager)
                getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        assert tm != null;
        String countryCode = tm.getSimCountryIso();
        phoneInputView.setEmptyDefault(countryCode);

        phoneInputView.setOnValidityChange(new IntlPhoneInput.IntlPhoneInputListener() {
            @Override
            public void done(View view, boolean isValid) {
                myInternationalNumber = phoneInputView.getNumber();
            }
        });

        return view;
    }

    private void submitRegistrationForm() {
        assert getActivity() != null;
        if (isNetworkAvailable(getActivity())) {
            if (isEmpty(editName)) editName.setError("Name required");
            else if (myInternationalNumber.length() == 0 || myInternationalNumber.equals(""))
                Toast.makeText(getActivity(), "Phone number required", Toast.LENGTH_SHORT).show();
            else if (myInternationalNumber.length() != 13 || !phoneInputView.isValid())
                Toast.makeText(getActivity(), "Invalid phone number", Toast.LENGTH_SHORT).show();
            else {
                // create message, post it to server and react based on received response or error
                try {
                    name = editName.getText().toString().trim();
                    email = editEmail.getText().toString().trim();
                    myInternationalNumber = phoneInputView.getNumber();
                    // Toast.makeText(getActivity(), name + ", " + email + ", " + myInternationalNumber, Toast.LENGTH_SHORT).show();

                    // register user by mutating(post) user's info to GraphQL server
                    MyApolloClient.getMyApolloClient().mutate(
                            AddCustomerMutation.builder()
                                    .name(name)
                                    .email(email)
                                    .phone(myInternationalNumber)
                                    .build()
                    ).enqueue(new ApolloCall.Callback<AddCustomerMutation.Data>() {
                        @Override
                        public void onResponse(@Nonnull Response<AddCustomerMutation.Data> response) {
                            // run on UI thread to update data instantaneously
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Profile created/edited successfully", Toast.LENGTH_SHORT).show();
                                    // redirect user to main activity
                                    exitToTargetActivity((AppCompatActivity) getActivity(), MainActivity.class);
                                }
                            });
                        }

                        @Override
                        public void onFailure(@Nonnull ApolloException e) {
                            // Log error so as to fix it
                            Log.e(ERROR, "onFailure: Something went wrong. " + e.getMessage());
                        }
                    });

                } catch (Exception ex) {
                    Log.e(ERROR, "Something went wrong");
                    ex.printStackTrace();
                    Toast.makeText(getActivity(), "Updating profile failed", Toast.LENGTH_SHORT).show();
                }
            }
        } else requestInternetAccess(getActivity());
    }
}
