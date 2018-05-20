package com.nnk.cryptoalert.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.nnk.cryptoalert.R;
import com.nnk.cryptoalert.adapters.TrackingAdapter;
import com.nnk.cryptoalert.tasks.IProgressUpdateView;
import com.nnk.cryptoalert.tasks.ITaskResultRecipient;
import com.nnk.cryptoalert.data.GlobalCoinData;
import com.nnk.cryptoalert.data.entities.Coin;
import com.nnk.cryptoalert.db.local.LocalDatabase;
import com.nnk.cryptoalert.db.local.entities.TrackedCoin;
import com.nnk.cryptoalert.db.local.entities.TrackedExchange;
import com.nnk.cryptoalert.db.local.entities.TrackedItem;

import java.util.*;

public class TrackingEditorFragment extends PausableFragment implements TrackingAdapter.ItemDeleter,
    ITaskResultRecipient<Map<String, Coin>> {

  private Map<String, Coin> allCoins;
  private ArrayList<String> allExchanges;

  private RecyclerView trackedCoinsView;
  private RecyclerView trackedExchangesView;
  private Button addCoinButton;
  private Button addExchangeButton;
  private AutoCompleteTextView newCoinAutoTextView;
  private AutoCompleteTextView newExchangeAutoTextView;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  @SuppressWarnings("unchecked")
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_tracking_editor, container, false);
    trackedCoinsView = view.findViewById(R.id.tracked_coins_recycler_view);
    trackedExchangesView = view.findViewById(R.id.tracked_exchanges_recycler_view);
    trackedCoinsView.setLayoutManager(new NonScrollableLinearLayoutManager(getActivity()));
    trackedExchangesView.setLayoutManager(new NonScrollableLinearLayoutManager(getActivity()));
    // Set temporary adapters until TrackedItems have been read from db
    trackedCoinsView.setAdapter(new TrackingAdapter(getActivity(), new ArrayList<TrackedItem>(), this));
    trackedExchangesView.setAdapter(new TrackingAdapter(getActivity(), new ArrayList<TrackedItem>(), this));
    addCoinButton = view.findViewById(R.id.add_tracked_coin_button);
    addExchangeButton = view.findViewById(R.id.add_tracked_exchange_button);
    newCoinAutoTextView = view.findViewById(R.id.add_tracked_coin_auto_text_view);
    newExchangeAutoTextView = view.findViewById(R.id.add_tracked_exchange_auto_text_view);
    // Load all exchanges from resource array
    allExchanges = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.exchange_names)));
    // Fetch tracked items from db and update items
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        final List<TrackedItem> trackedCoins = (List<TrackedItem>)(Object)
            LocalDatabase.getInstance(getContext()).trackedDao().getTrackedCoins();
        final List<TrackedItem> trackedExchanges = (List<TrackedItem>)(Object)
            LocalDatabase.getInstance(getContext()).trackedDao().getTrackedExchanges();
        TrackingEditorFragment.this.getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            setTrackedItems(trackedCoins, trackedExchanges);
          }
        });
      }
    });
    // Start fetching all coins from API, onTaskDone(..) will be called when done
    GlobalCoinData.getAllCoins(getContext(), (IProgressUpdateView<Integer>) getActivity(), this);
    return view;
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  /**
   * Deletes TrackedItem from database.
   * @param item
   */
  @Override
  public void deleteItem(final TrackedItem item) {
    if (item instanceof TrackedCoin) {
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
          LocalDatabase.getInstance(getContext()).trackedDao().deleteTrackedCoins((TrackedCoin) item);
        }
      });
      Toast.makeText(getContext(), "Coin " + item.name + " has been removed.", Toast.LENGTH_SHORT).show();
    } else if (item instanceof TrackedExchange) {
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
          LocalDatabase.getInstance(getContext()).trackedDao().deleteTrackedExchanges((TrackedExchange) item);
        }
      });
      Toast.makeText(getContext(), "Exchange " + item.name + " has been removed.", Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Called when all coins have been fetched.
   * @param allCoins
   */
  @Override
  @SuppressWarnings("unchecked")
  public void onTaskDone(Map<String, Coin> allCoins) {
    this.allCoins = allCoins;
    if (allCoins.isEmpty()) {
      Toast.makeText(getContext(), "Could not fetch coins.", Toast.LENGTH_LONG).show();
    }
    // Set AutoTextView autocomplete with all coins and exchanges
    Set<String> allCoinNames = this.allCoins.keySet();
    ArrayAdapter<String> allCoinsListAdapter = new ArrayAdapter<String>(getActivity(),
        android.R.layout.simple_expandable_list_item_1, allCoinNames.toArray(new String[allCoinNames.size()]));
    ArrayAdapter<String> allExchangesListAdapter = new ArrayAdapter<String>(getActivity(),
        android.R.layout.simple_expandable_list_item_1, allExchanges);
    newCoinAutoTextView.setAdapter(allCoinsListAdapter);
    newExchangeAutoTextView.setAdapter(allExchangesListAdapter);


  }

  /**
   * Updates UI with elements from db. To be called by Room query thread when entities are fetched.
   * @param trackedCoins
   * @param trackedExchanges
   */
  private void setTrackedItems(List<TrackedItem> trackedCoins, List<TrackedItem> trackedExchanges) {
    // Create adapters
    final TrackingAdapter trackedCoinsAdapter = new TrackingAdapter(getActivity(), trackedCoins, this);
    trackedCoinsView.setAdapter(trackedCoinsAdapter);
    final TrackingAdapter trackedExchangesAdapter = new TrackingAdapter(getActivity(), trackedExchanges, this);
    trackedExchangesView.setAdapter(trackedExchangesAdapter);

    // Adding new tracked items
    addCoinButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String coinName = newCoinAutoTextView.getText().toString().trim();
        // Check if coin is already tracked
        for (TrackedItem item : trackedCoinsAdapter.getTrackedItems()) {
          if (item.name.equals(coinName)) {
            Toast.makeText(getContext(), "Coin already tracked.", Toast.LENGTH_LONG).show();
            return;
          }
        }
        // Check if coin exists
        Coin coin = allCoins.get(coinName);
        if (coin == null) {
          Toast.makeText(getContext(), "Coin does not exist.", Toast.LENGTH_LONG).show();
          return;
        }
        final TrackedCoin trackedCoin = new TrackedCoin();
        trackedCoin.name = coin.getName();
        // Add coin to database and RecyclerView
        AsyncTask.execute(new Runnable() {
          @Override
          public void run() {
            LocalDatabase.getInstance(getContext()).trackedDao().addTrackedCoin(trackedCoin);
          }
        });
        // Update UI
        trackedCoinsAdapter.addItem(trackedCoin);
        newCoinAutoTextView.getText().clear();
        Toast.makeText(getContext(), "Coin " + trackedCoin.name + " is now tracked.", Toast.LENGTH_SHORT).show();
      }
    });
    addExchangeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String exchangeName = newExchangeAutoTextView.getText().toString().trim();
        // Check if exchange is already tracked
        for (TrackedItem item : trackedExchangesAdapter.getTrackedItems()) {
          if (item.name.equals(exchangeName)) {
            Toast.makeText(getContext(), "Exchange already tracked.", Toast.LENGTH_LONG).show();
            return;
          }
        }
        // Check if exchange exists
        if (!allExchanges.contains(exchangeName)) {
          Toast.makeText(getContext(), "Exchange doesn't exist.", Toast.LENGTH_LONG).show();
          return;
        }
        final TrackedExchange trackedExchange = new TrackedExchange();
        trackedExchange.name = exchangeName;
        // Add Exchange to database and RecyclerView
        AsyncTask.execute(new Runnable() {
          @Override
          public void run() {
            LocalDatabase.getInstance(getContext()).trackedDao().addTrackedExchange(trackedExchange);
          }
        });
        trackedExchangesAdapter.addItem(trackedExchange);
        newExchangeAutoTextView.getText().clear();
        Toast.makeText(getContext(), "Exchange " + trackedExchange.name + " was added.", Toast.LENGTH_SHORT).show();
      }
    });
  }

  /**
   * Called by parent Activity when task is executing.
   * @param enabled
   */
  @Override
  public void setEnabled(boolean enabled) {
    trackedCoinsView.setEnabled(enabled);
    trackedExchangesView.setEnabled(enabled);
    addCoinButton.setEnabled(enabled);
    addExchangeButton.setEnabled(enabled);
    newCoinAutoTextView.setEnabled(enabled);
    newExchangeAutoTextView.setEnabled(enabled);
  }

  private class NonScrollableLinearLayoutManager extends LinearLayoutManager {

    public NonScrollableLinearLayoutManager(Context context) {
      super(context);
    }

    @Override
    public boolean canScrollVertically() {
      return false;
    }
  }
}
