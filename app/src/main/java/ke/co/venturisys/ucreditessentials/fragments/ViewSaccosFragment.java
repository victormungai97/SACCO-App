package ke.co.venturisys.ucreditessentials.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import ke.co.ke.venturisys.ucreditessentials.ViewListOfSaccosQuery;
import ke.co.venturisys.ucreditessentials.R;
import ke.co.venturisys.ucreditessentials.others.MyApolloClient;
import ke.co.venturisys.ucreditessentials.others.Sacco;
import ke.co.venturisys.ucreditessentials.others.SaccosLinearAdapter;

import static ke.co.venturisys.ucreditessentials.others.Constants.ERROR;
import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_SACCO_OVERVIEW;
import static ke.co.venturisys.ucreditessentials.others.Extras.changeFragment;

public class ViewSaccosFragment extends Fragment {

    View view;
    SaccosLinearAdapter adapter; // communicates with recycler view
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager; // lays out children in a linear format

    List<Sacco> saccos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        assert getActivity() != null;

        // inflate layout
        view = inflater.inflate(R.layout.fragment_view_saccos, container, false);
        saccos = new ArrayList<>();

        // set up recycler view
        recyclerView = view.findViewById(R.id.view_saccos_recycler_view);
        adapter = new SaccosLinearAdapter(getActivity(), saccos);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        getSaccos();

        return view;
    }

    /*
     * This method queries for saccos using GraphQL and adds them to the adapter
     */
    private void getSaccos() {
        assert getActivity() != null;
        MyApolloClient.getMyApolloClient().query(
                ViewListOfSaccosQuery.builder().build()
        ).enqueue(new ApolloCall.Callback<ViewListOfSaccosQuery.Data>() {
            @Override
            public void onResponse(@Nonnull final Response<ViewListOfSaccosQuery.Data> response) {
                // run changes on UI thread
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // update list of saccos
                        if (response.data() != null) {
                            List<ViewListOfSaccosQuery.AllSaccose> allSaccoses =
                                    Objects.requireNonNull(response.data()).allSaccoses();
                            for (ViewListOfSaccosQuery.AllSaccose saccose : allSaccoses) {
                                saccos.add(new Sacco(saccose.icon(), saccose.name(), saccose.creationDate(), saccose.status()));
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                // Log error so as to fix it
                Log.e(ERROR, "onFailure: Something went wrong. " + e.getMessage());
            }
        });

        adapter = new SaccosLinearAdapter(getActivity(), saccos);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeFragment(new SaccoOverviewFragment(), new Handler(), TAG_SACCO_OVERVIEW,
                        (AppCompatActivity) getActivity());
            }
        });
    }

}
