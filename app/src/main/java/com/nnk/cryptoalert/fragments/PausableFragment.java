package com.nnk.cryptoalert.fragments;

import android.support.v4.app.Fragment;

/**
 * Created by Nick on 19/05/2018.
 */

public abstract class PausableFragment extends Fragment {

  /**
   * Enables or disables all Views in this fragment.
   * @param enabled
   */
  public abstract void setEnabled(boolean enabled);
}
