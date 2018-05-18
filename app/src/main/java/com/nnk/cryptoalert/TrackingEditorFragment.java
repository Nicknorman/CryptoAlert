package com.nnk.cryptoalert;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.nnk.cryptoalert.db.local.entities.TrackedCoin;
import com.nnk.cryptoalert.db.local.entities.TrackedExchange;
import com.nnk.cryptoalert.db.local.entities.TrackedItem;

import java.util.ArrayList;
import java.util.List;

public class TrackingEditorFragment extends Fragment {

  List<TrackedCoin> trackedCoins;
  List<TrackedExchange> trackedExchanges;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_tracking_editor,container, false);

    RecyclerView trackedCoinsView = view.findViewById(R.id.tracked_coins_recycler_view);
    RecyclerView trackedExchangesView = view.findViewById(R.id.tracked_exchanges_recycler_view);
    ArrayAdapter<String> allCoinsListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, new String[] {"Coin1", "Coin2"});
    ArrayAdapter<String> allExchangesListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, new String[] {"Exchange1", "Exchange2"});
    AutoCompleteTextView newCoinAutoTextView = view.findViewById(R.id.add_tracked_coin_auto_text_view);
    AutoCompleteTextView newExchangeAutoTextView = view.findViewById(R.id.add_tracked_exchange_auto_text_view);
    newCoinAutoTextView.setAdapter(allCoinsListAdapter);
    newExchangeAutoTextView.setAdapter(allExchangesListAdapter);
    Button addCoinButton = view.findViewById(R.id.add_tracked_coin_button);
    Button addExchangeButton = view.findViewById(R.id.add_tracked_exchange_button);



    trackedCoins = new ArrayList();
    TrackedCoin coin1 = new TrackedCoin();
    coin1.id = 0;
    coin1.name = "Test1";
    TrackedCoin coin2 = new TrackedCoin();
    coin2.id = 1;
    coin2.name = "Test2";
    trackedCoins.add(coin1);
    trackedCoins.add(coin2);
    TrackingAdapter trackedCoinsAdapter = new TrackingAdapter(getActivity(), trackedCoins);
    trackedCoinsView.setAdapter(trackedCoinsAdapter);
    trackedCoinsView.setLayoutManager(new LinearLayoutManager(getActivity()));

    trackedExchanges = new ArrayList();
    TrackedExchange ex1 = new TrackedExchange();
    ex1.id = 0;
    ex1.name = "Exchange1";
    TrackedExchange ex2 = new TrackedExchange();
    ex2.id = 1;
    ex2.name = "Exchange2";
    trackedExchanges.add(ex1);
    trackedExchanges.add(ex2);
    TrackingAdapter trackedExchangesAdapter = new TrackingAdapter(getActivity(), trackedExchanges);
    trackedExchangesView.setAdapter(trackedExchangesAdapter);
    trackedExchangesView.setLayoutManager(new LinearLayoutManager(getActivity()));

    return view;
  }

  @Override
  public void onPause() {
    super.onPause();
  }
}
