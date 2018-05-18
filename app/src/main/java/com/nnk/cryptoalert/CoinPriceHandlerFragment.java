package com.nnk.cryptoalert;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by Nick on 24-03-2018.
 */

public class CoinPriceHandlerFragment extends Fragment {
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    setRetainInstance(true); // Will not be recreated during configuration changes
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onPause() {
    super.onPause();
  }
}
