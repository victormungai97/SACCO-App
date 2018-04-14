package ke.co.venturisys.ucreditessentials.fragments;


import android.graphics.PorterDuff;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Objects;

import javax.annotation.Nonnull;

import ke.co.ke.venturisys.ucreditessentials.FindCustomerQuery;
import ke.co.venturisys.ucreditessentials.R;
import ke.co.venturisys.ucreditessentials.others.MyApolloClient;

import static ke.co.venturisys.ucreditessentials.activities.MainActivity.setCurrentTag;
import static ke.co.venturisys.ucreditessentials.activities.MainActivity.setNavItemIndex;
import static ke.co.venturisys.ucreditessentials.others.Constants.ERROR;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_BALANCE_STATEMENT;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_EDIT_PROFILE;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_SACCO_LIST;
import static ke.co.venturisys.ucreditessentials.others.Constants.URL;
import static ke.co.venturisys.ucreditessentials.others.Extras.changeFragment;
import static ke.co.venturisys.ucreditessentials.others.Extras.isNetworkAvailable;
import static ke.co.venturisys.ucreditessentials.others.Extras.loadPictureToImageView;
import static ke.co.venturisys.ucreditessentials.others.Extras.requestInternetAccess;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    View view;
    ImageView imageViewProfile, imageViewHome;
    Button viewSacco, editProfile;
    TextView textViewName;
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseUser user;
    String email, phoneNumber;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        assert getActivity() != null;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // inflate widgets
        imageViewProfile = view.findViewById(R.id.img_profile_page);
        imageViewHome = view.findViewById(R.id.btnProfileHome);
        textViewName = view.findViewById(R.id.tvProfileName);
        editProfile = view.findViewById(R.id.edit_profile);
        viewSacco = view.findViewById(R.id.view_saccos);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(getResources().getColor(R.color.colorProgressBar), PorterDuff.Mode.SRC_IN);

        // redirect to homepage
        imageViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNavItemIndex(0);
                setCurrentTag(TAG_BALANCE_STATEMENT);
                changeFragment(new BalanceStatementFragment(), new Handler(), TAG_BALANCE_STATEMENT,
                        (AppCompatActivity) getActivity());
            }
        });

        // redirect to sacco page
        viewSacco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new ViewSaccosFragment(), new Handler(), TAG_SACCO_LIST, (AppCompatActivity) getActivity());
            }
        });

        // redirect to edit profile page
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(EditProfileFragment.newInstance(
                        textViewName.getText().toString().trim(),
                        email,
                        phoneNumber),
                        new Handler(),
                        TAG_EDIT_PROFILE, (AppCompatActivity) getActivity());
            }
        });

        if (isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
            updateProfile();
        } else requestInternetAccess(getActivity());

        return view;
    }

    /*
     * This method queries the Customer table and returns all the details of the user
     * Please ensure that the user is logged in and there is internet connection
     */
    private void updateProfile() {
        assert getActivity() != null;
        progressBar.setVisibility(View.VISIBLE);
        if (user != null) {
            MyApolloClient.getMyApolloClient().query(
                    FindCustomerQuery.builder().email(Objects.requireNonNull(user.getEmail())).build()
            ).enqueue(new ApolloCall.Callback<FindCustomerQuery.Data>() {
                @Override
                public void onResponse(@Nonnull final Response<FindCustomerQuery.Data> response) {
                    // update UI on activity's thread
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // hide progress bar
                            progressBar.setVisibility(View.GONE);
                            // update text views
                            textViewName.setText(Objects.requireNonNull(
                                    Objects.requireNonNull(response.data()).Customer()).name());
                            email = Objects.requireNonNull(Objects.requireNonNull(
                                    response.data()).Customer()).email();
                            phoneNumber = Objects.requireNonNull(Objects.requireNonNull(
                                    response.data()).Customer()).phone();
                            // load profile image
                            String photo_url = Objects.requireNonNull(Objects.requireNonNull(response.data()).Customer()).picture();
                            HashMap<String, Object> src = new HashMap<>();
                            src.put(URL, photo_url);
                            loadPictureToImageView(src, R.drawable.avatar, imageViewProfile, true, false,
                                    false, true);
                        }
                    });
                }

                @Override
                public void onFailure(@Nonnull ApolloException e) {
                    // Log error so as to fix it
                    Log.e(ERROR, "onFailure: Something went wrong. " + e.getMessage());
                }
            });
        }
    }

}
