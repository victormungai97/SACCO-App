package ke.co.venturisys.ucreditessentials.others;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;

import ke.co.venturisys.ucreditessentials.R;
import ke.co.venturisys.ucreditessentials.fragments.SaccoOverviewFragment;

import static ke.co.venturisys.ucreditessentials.others.Constants.TAG_SACCO_OVERVIEW;
import static ke.co.venturisys.ucreditessentials.others.Constants.URL;
import static ke.co.venturisys.ucreditessentials.others.Extras.changeFragment;
import static ke.co.venturisys.ucreditessentials.others.Extras.loadPictureToImageView;

/**
 * This class implements the Adapter for the recyclerview that shall contain
 * a list of SACCOs that are registered to the system
 */

public class SaccosLinearAdapter extends RecyclerViewAdapter {

    private Activity activity;
    private List<Sacco> saccos;

    public SaccosLinearAdapter(Activity activity, List<Sacco> saccos) {
        this.activity = activity;
        this.saccos = saccos;
    }

    /*
     * Creates the view holder that shall be passed to the recycler
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.saccos_page_card, parent, false);

        return new Holder(view);
    }

    /*
     * Set values to views and bind to recyclerview
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Holder myHolder = (Holder) holder;
        final Sacco sacco = saccos.get(position);

        myHolder.name.setText(sacco.getName());
        myHolder.date.setText(sacco.getCreation_date());
        myHolder.status.setText(StringUtils.capitalize(sacco.getStatus()));
        if (sacco.getStatus().equals("active")) myHolder.status.setTextColor(Color.BLUE);

        HashMap<String, Object> src = new HashMap<>();
        src.put(URL, sacco.getIcon());
        loadPictureToImageView(src, R.drawable.avatar, myHolder.icon, true, false,
                false, false);

        myHolder.redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new SaccoOverviewFragment(), new Handler(), TAG_SACCO_OVERVIEW,
                        (AppCompatActivity) activity);
            }
        });

    }

    /*
     * Gives the number of saccos in list
     */
    @Override
    public int getItemCount() {
        return saccos.size();
    }

    public class Holder extends RecyclerViewAdapter.RecycleViewHolder {

        ImageView icon, redirect;
        TextView name, status, date;

        Holder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.sacco_icon);
            name = itemView.findViewById(R.id.sacco_title);
            status = itemView.findViewById(R.id.sacco_status);
            date = itemView.findViewById(R.id.sacco_creation_date);
            redirect = itemView.findViewById(R.id.sacco_see_more);
        }
    }
}
