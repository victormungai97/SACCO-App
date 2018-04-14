package ke.co.venturisys.ucreditessentials.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import ke.co.venturisys.ucreditessentials.R;

import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_SACCO_PRODUCTS;
import static ke.co.venturisys.ucreditessentials.others.Constants.URL;
import static ke.co.venturisys.ucreditessentials.others.Extras.changeFragment;
import static ke.co.venturisys.ucreditessentials.others.Extras.loadPictureToImageView;

public class SaccoOverviewFragment extends Fragment {

    View view;
    TextView type, registration, registrationDate, category, agmDate,
            county, country, management, reg_no, products_link;
    ImageView backgroundIv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // inflate the layout of this fragment
        view = inflater.inflate(R.layout.fragment_sacco_overview, container, false);
        assert getActivity() != null;

        initCollapsingToolbar();

        // initialise widgets
        type = view.findViewById(R.id.type_details);
        registration = view.findViewById(R.id.registered_details);
        registrationDate = view.findViewById(R.id.registration_date_details);
        category = view.findViewById(R.id.category_details);
        agmDate = view.findViewById(R.id.agm_details);
        county = view.findViewById(R.id.county_details);
        country = view.findViewById(R.id.country_details);
        management = view.findViewById(R.id.management_details);
        reg_no = view.findViewById(R.id.sacco_regno_details);
        products_link = view.findViewById(R.id.sacco_products_link);
        backgroundIv = view.findViewById(R.id.view_saccos_backdrop);

        // redirect to sacco products link
        products_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new SaccoProductFragment(), new Handler(), TAG_SACCO_PRODUCTS,
                        (AppCompatActivity) getActivity());
            }
        });

        // set background image
        HashMap<String, Object> src = new HashMap<>();
        src.put(URL, "http://fenesi.com/wp-content/uploads/2017/06/Saccos-660x330.jpg");
        loadPictureToImageView(src, R.drawable.avatar, backgroundIv, false, false,
                false, false);

        return view;
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.app_name));
        AppBarLayout appBarLayout = view.findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
}
