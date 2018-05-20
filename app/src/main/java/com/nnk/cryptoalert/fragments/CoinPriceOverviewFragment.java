package com.nnk.cryptoalert.fragments;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;
import com.nnk.cryptoalert.R;
import com.nnk.cryptoalert.adapters.CoinPriceAdapter;
import com.nnk.cryptoalert.data.GlobalCoinData;
import com.nnk.cryptoalert.data.entities.Coin;
import com.nnk.cryptoalert.data.entities.CoinPriceEntry;
import com.nnk.cryptoalert.data.entities.HistoricCoinPrice;
import com.nnk.cryptoalert.data.entities.HistoricPrice;
import com.nnk.cryptoalert.db.local.LocalDatabase;
import com.nnk.cryptoalert.db.local.entities.TrackedCoin;
import com.nnk.cryptoalert.db.local.entities.TrackedExchange;
import com.nnk.cryptoalert.tasks.HistoricPriceRequest;
import com.nnk.cryptoalert.tasks.IProgressUpdateView;
import com.nnk.cryptoalert.tasks.ITaskResultRecipient;
import com.nnk.cryptoalert.tools.ChartBuilder;

import java.util.*;

/**
 * Created by Nick on 24-03-2018.
 */

public class CoinPriceOverviewFragment extends PausableFragment implements SwipeRefreshLayout.OnRefreshListener {

  private enum HistoryLength { H1, D1, D7, D30 }

  private Map<String, Coin> allCoins;
  private List<TrackedCoin> trackedCoins;
  private List<TrackedExchange> trackedExchanges;
  private Map<String, Bitmap> coinImages;
  private Map<String, HistoricCoinPrice> currentHistoCoinPrices;
  private HistoryLength currentHistoryLength = HistoryLength.D1;

  private RecyclerView coinPriceView;
  private CoinPriceAdapter coinPriceAdapter;
  private RadioGroup historyLengthToggleGroup;
  private CompoundButton h1ToggleButton;
  private CompoundButton d1ToggleButton;
  private CompoundButton d7ToggleButton;
  private CompoundButton m1ToggleButton;
  private SwipeRefreshLayout swipeRefresher;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  @SuppressWarnings("unchecked")
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_coin_price_overview, container, false);
    coinPriceView = view.findViewById(R.id.coin_price_recycler_view);
    coinPriceView.setLayoutManager(new LinearLayoutManager(getActivity()));
    coinPriceAdapter = new CoinPriceAdapter(getActivity(), new ArrayList<CoinPriceEntry>());
    coinPriceView.setAdapter(coinPriceAdapter);
    historyLengthToggleGroup = view.findViewById(R.id.history_length_toggle_group);
    // Create RadioGroup behavior
    historyLengthToggleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
          ToggleButton btn = (ToggleButton) group.getChildAt(i);
          btn.setChecked(btn.getId() == checkedId);
          HistoryLength oldHistoryLength = currentHistoryLength;
          switch (checkedId) {
            case R.id.h1_toggle_button:
              currentHistoryLength = HistoryLength.H1;
              break;
            case R.id.d1_toggle_button:
              currentHistoryLength = HistoryLength.D1;
              break;
            case R.id.d7_toggle_button:
              currentHistoryLength = HistoryLength.D7;
              break;
            case R.id.m1_toggle_button:
              currentHistoryLength = HistoryLength.D30;
              break;
            default:
              currentHistoryLength = HistoryLength.H1;
          }
          // Only update if different. The user can update via Swipe if necessary.
          if (oldHistoryLength != currentHistoryLength) {
            updateHistorialPrices();
          }
        }
      }
    });
    // Create toggle buttons with listener
    h1ToggleButton = view.findViewById(R.id.h1_toggle_button);
    d1ToggleButton = view.findViewById(R.id.d1_toggle_button);
    d7ToggleButton = view.findViewById(R.id.d7_toggle_button);
    m1ToggleButton = view.findViewById(R.id.m1_toggle_button);
    View.OnClickListener toggleHistoricalLength = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ((RadioGroup)view.getParent()).check(view.getId());
      }
    };
    h1ToggleButton.setOnClickListener(toggleHistoricalLength);
    d1ToggleButton.setOnClickListener(toggleHistoricalLength);
    d7ToggleButton.setOnClickListener(toggleHistoricalLength);
    m1ToggleButton.setOnClickListener(toggleHistoricalLength);
    // Set this as listener for swipeRefresher so that coin prices are updated whenever it is used
    swipeRefresher = view.findViewById(R.id.swipe_refresh_coin_prices);
    swipeRefresher.setOnRefreshListener(this);
    // Fetch tracked items from db and update data
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        trackedCoins = LocalDatabase.getInstance(getContext()).trackedDao().getTrackedCoins();
        trackedExchanges = LocalDatabase.getInstance(getContext()).trackedDao().getTrackedExchanges();
        if (coinImages == null) {
          updateCoinImages();
        }
        updateHistorialPrices();
      }
    });
    // Create recipient for all coin data and start task
    ITaskResultRecipient<Map<String, Coin>> allCoinsRecipient = new ITaskResultRecipient<Map<String, Coin>>() {
      @Override
      public void onTaskDone(Map<String, Coin> stringCoinMap) {
        allCoins = stringCoinMap;
        if (coinImages == null) {
          updateCoinImages();
        }
      }
    };
    GlobalCoinData.getAllCoins(getContext(), (IProgressUpdateView<Integer>) getActivity(), allCoinsRecipient);
    return view;
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  /**
   * Called by swipeRefresher. Updates coin price data.
   */
  @Override
  public void onRefresh() {
    updateHistorialPrices();
  }

  /**
   * Should be called when all coin data and tracked coins and exchanges task results have been returned. Will fetch
   * prices when tracked coins and exchanges have been set. Will download images when URLs and tracked coins are set.
   */
  @SuppressWarnings("unchecked")
  private void updateCoinImages() {
    // Check if it's possible to download images
    if (allCoins != null && trackedCoins != null) {
      // Create recipient for coin images
      ITaskResultRecipient<Map<String, Bitmap>> imageRecipient = new ITaskResultRecipient<Map<String, Bitmap>>() {
        @Override
        public void onTaskDone(Map<String, Bitmap> stringBitmapMap) {
          coinImages = stringBitmapMap;
          updateAdapter();
        }
      };
      Map<String, String> urlMap = new HashMap<>();
      for (TrackedCoin coin : trackedCoins) {
        urlMap.put(coin.name, allCoins.get(coin.name).getImageUrl());
      }
      GlobalCoinData.getCoinImages(urlMap, (IProgressUpdateView<Integer>) getActivity(), imageRecipient);
    }
  }

  private void updateHistorialPrices() {
    // Check if it's possible to get historical prices
    if (trackedCoins != null && trackedExchanges != null) {
      // Create recipient for historical price data
      ITaskResultRecipient<Map<String, HistoricCoinPrice>> histoRecipient
          = new ITaskResultRecipient<Map<String, HistoricCoinPrice>>() {
        @Override
        public void onTaskDone(Map<String, HistoricCoinPrice> stringHistoricCoinPriceMap) {
          currentHistoCoinPrices = stringHistoricCoinPriceMap;
          updateAdapter();
        }
      };
      // Create request parameters
      HistoricPriceRequest.HistoryType histType;
      int period;
      int aggregate = 1;
      switch (currentHistoryLength) {
        case H1:
          histType = HistoricPriceRequest.HistoryType.MINUTE;
          period = 20;
          aggregate = 3;
          break;
        case D1:
          histType = HistoricPriceRequest.HistoryType.HOUR;
          period = 24;
          aggregate = 1;
          break;
        case D7:
          histType = HistoricPriceRequest.HistoryType.HOUR;
          period = 28;
          aggregate = 6;
          break;
        case D30:
          histType = HistoricPriceRequest.HistoryType.DAY;
          period = 30;
          break;
        default:
          histType = HistoricPriceRequest.HistoryType.MINUTE;
          period = 20;
          aggregate = 3;
      }
      String[] coinNames = new String[trackedCoins.size()];
      for (int i = 0; i < coinNames.length; i++) {
        coinNames[i] = trackedCoins.get(i).name;
      }
      String[] exchangeNames = new String[trackedExchanges.size()];
      for (int i = 0; i < exchangeNames.length; i++) {
        exchangeNames[i] = trackedExchanges.get(i).name;
      }
      String[] toCoins = getResources().getStringArray(R.array.histo_to_coins);
      HistoricPriceRequest request = new HistoricPriceRequest(histType, period, aggregate, coinNames, exchangeNames, toCoins);
      GlobalCoinData.getHistoricCoinPrices(getContext(), request, (IProgressUpdateView<Integer>) getActivity(),
          histoRecipient);
    }
  }

  /**
   * Creates list of CoinPriceEntry and updates Adapter, on the UI thread.
   */
  private void updateAdapter() {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        swipeRefresher.setRefreshing(false);
        ArrayList<CoinPriceEntry> entries = createCoinPriceEntries();
        coinPriceAdapter.updateEntries(entries);
      }
    });
  }

  private ArrayList<CoinPriceEntry> createCoinPriceEntries() {
    ArrayList<CoinPriceEntry> entries = new ArrayList<>();
    for (TrackedCoin trackedCoin : trackedCoins) {
      String name = trackedCoin.name;
      CoinPriceEntry entry = new CoinPriceEntry();
      entry.setName(name);
      if (currentHistoCoinPrices != null) {
        HistoricCoinPrice histCoinPrice = currentHistoCoinPrices.get(name);
        if (histCoinPrice != null) {
          if (histCoinPrice.getCurrentPrice() != null) {
            entry.setPriceUSD(histCoinPrice.getCurrentPrice().get("USD"));
          }
          HistoricPrice[] histPrices = histCoinPrice.getHistoricPrices().get("USD");
          if (histPrices != null && histPrices.length > 0) {
            // Calculate change in percentage
            double newPrice = histPrices[histPrices.length - 1].price;
            double oldPrice = histPrices[0].price;
            double change = ((newPrice - oldPrice) / oldPrice * 100d);
            entry.setChange(change);
            // Create chart data
            entry.setChartData(ChartBuilder.buildHistoricalChartData(histPrices, getContext()));
          }
        }
      }
      if (coinImages != null) {
        entry.setImage(coinImages.get(name));
      }
      entries.add(entry);
    }
    return entries;
  }

  @Override
  public void setEnabled(final boolean enabled) {
    coinPriceView.setEnabled(enabled);
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        h1ToggleButton.setEnabled(enabled);
        d1ToggleButton.setEnabled(enabled);
        d7ToggleButton.setEnabled(enabled);
        m1ToggleButton.setEnabled(enabled);
      }
    });
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (savedInstanceState != null) {
      ArrayList<CoinPriceEntry> entries = savedInstanceState.getParcelableArrayList("COIN_PRICE_ENTRIES");
      if (entries != null) {
        coinPriceAdapter.updateEntries(entries);
      }
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    ArrayList<CoinPriceEntry> entries = coinPriceAdapter.getEntries();
    if (entries != null) {
      outState.putParcelableArrayList("COIN_PRICE_ENTRIES", entries);
    }
  }
}
