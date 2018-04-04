package ke.co.venturisys.ucreditessentials.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ke.co.venturisys.ucreditessentials.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    View view;
    ImageView facebookBtn, googleBtn;

    public RegisterFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate layout
        view = inflater.inflate(R.layout.fragment_register, container, false);
        assert getActivity() != null;

        facebookBtn = view.findViewById(R.id.facebook_btn);
        googleBtn = view.findViewById(R.id.google_plus_btn);

        return view;
    }

}
