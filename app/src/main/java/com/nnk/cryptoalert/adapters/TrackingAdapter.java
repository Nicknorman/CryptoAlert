package com.nnk.cryptoalert.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nnk.cryptoalert.R;
import com.nnk.cryptoalert.db.local.entities.TrackedItem;

import java.util.List;

public class TrackingAdapter extends RecyclerView.Adapter<TrackingAdapter.ViewHolder> {

  private Context context;
  private List<TrackedItem> trackedItems;
  private ItemDeleter itemDeleter;

  public TrackingAdapter(Context context, List<TrackedItem> trackedItems, ItemDeleter itemDeleter) {
    this.context = context;
    this.trackedItems = trackedItems;
    this.itemDeleter = itemDeleter;
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
    View trackingEntryView = inflater.inflate(R.layout.entry_tracked_item, parent, false);
    ViewHolder viewHolder = new ViewHolder(trackingEntryView);
    return viewHolder;
  }

  /**
   * Populates data into the item through holder
   * @param holder
   * @param position
   */
  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    final TrackedItem trackedItem = trackedItems.get(position);
    TextView nameTextView = holder.nameTextView;
    nameTextView.setText(trackedItem.name);
    Button deleteButton = holder.deleteButton;
    // Delete TrackedItem from DB and RecyclerView
    deleteButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        new AlertDialog.Builder(context)
            .setMessage("Stop tracking " + trackedItem.name + "?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                itemDeleter.deleteItem(trackedItem);
                trackedItems.remove(trackedItem);
                notifyItemRemoved(holder.getAdapterPosition());
              }
            }).setNegativeButton(android.R.string.no, null)
            .show();
      }
    });
  }

  @Override
  public int getItemCount() {
    return trackedItems.size();
  }

  public List<TrackedItem> getTrackedItems() {
    return trackedItems;
  }

  public void addItem(TrackedItem item) {
    trackedItems.add(item);
    notifyItemInserted(trackedItems.size() - 1);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    TextView nameTextView;
    Button deleteButton;

    ViewHolder(View itemView) {
      super(itemView);
      nameTextView = itemView.findViewById(R.id.tracked_text_view);
      deleteButton = itemView.findViewById(R.id.tracked_delete_button);
    }
  }

  /**
   * Should be implemented by class that is responsible for deleting TrackedItems in local db.
   */
  public interface ItemDeleter {
    public void deleteItem(TrackedItem item);
  }
}
