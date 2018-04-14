package ke.co.venturisys.ucreditessentials.others;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

/*
 * Inherits from RecyclerView Adapter
 * It is responsible for creating the necessary ViewHolder
 * and binding ViewHolder to data from the model layer
 * It also implements item click or item long click event
 */

public abstract class RecyclerViewAdapter extends RecyclerView.Adapter {

    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemLongClickListener onItemLongClickListener;

    // implement item click listener
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(RecycleViewHolder itemHolder) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

    // implement item long click listener
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    private void onItemHolderLongClick(RecycleViewHolder itemHolder) {
        if (onItemLongClickListener != null) {
            onItemLongClickListener.onItemLongClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

    /**
     * Holder class to wire up the views in specified layout files
     * Its responsibility is to hold on to a given View
     */
    class RecycleViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        RecycleViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemHolderClick(this);
        }

        @Override
        public boolean onLongClick(View v) {
            onItemHolderLongClick(this);
            return true;
        }

    }

}
