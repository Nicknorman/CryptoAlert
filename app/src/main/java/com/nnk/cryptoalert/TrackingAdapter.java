package com.nnk.cryptoalert;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nnk.cryptoalert.db.local.entities.TrackedCoin;
import com.nnk.cryptoalert.db.local.entities.TrackedItem;

import java.util.List;

public class TrackingAdapter extends RecyclerView.Adapter<TrackingAdapter.ViewHolder> {

  private Context context;
  private List<? extends TrackedItem> trackedItems;

  public TrackingAdapter(Context context, List<? extends TrackedItem> trackedItems) {
    this.context = context;
    this.trackedItems = trackedItems;
  }

  /**
   * Inflating a layout from XML and returning the holder.
   * @param parent
   * @param viewType
   * @return
   */
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View trackingEntryView = inflater.inflate(R.layout.view_tracking_entry, parent, false);
    ViewHolder viewHolder = new ViewHolder(trackingEntryView);
    return viewHolder;
  }

  /**
   * Populates data into the item through holder
   * @param holder
   * @param position
   */
  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    TrackedItem trackedItem = trackedItems.get(position);
    TextView nameTextView = holder.nameTextView;
    nameTextView.setText(trackedItem.name);
  }

  @Override
  public int getItemCount() {
    return trackedItems.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public TextView nameTextView;
    public Button deleteButton;

    public ViewHolder(View itemView) {
      super(itemView);
      nameTextView = itemView.findViewById(R.id.tracked_text_view);
      deleteButton = itemView.findViewById(R.id.tracked_delete_button);
    }
  }
}
