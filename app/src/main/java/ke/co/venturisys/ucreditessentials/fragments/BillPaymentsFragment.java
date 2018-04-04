package ke.co.venturisys.ucreditessentials.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ke.co.venturisys.ucreditessentials.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BillPaymentsFragment extends Fragment {


    public BillPaymentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bill_payments, container, false);
    }

}
