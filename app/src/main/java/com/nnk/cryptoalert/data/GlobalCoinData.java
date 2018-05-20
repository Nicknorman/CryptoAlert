package com.nnk.cryptoalert.data;

import android.content.Context;
import android.graphics.Bitmap;
import com.nnk.cryptoalert.R;
import com.nnk.cryptoalert.data.entities.HistoricCoinPrice;
import com.nnk.cryptoalert.tasks.*;
import com.nnk.cryptoalert.data.entities.Coin;

import java.util.*;

/**
 * Created by Nick on 19/05/2018.
 */

public class GlobalCoinData {

  private static Map<String, Coin> allCoins;
  private static Map<String, Bitmap> coinImages = new HashMap();

  /**
   * Will fetch from API if not previously done, using a separate thread, and deliver the result to specified
   * ITaskResultRecipient.
   * @param loaderView the View that should show a loading screen in case data needs to be fetched.
   * @return basic data for all coins mapped to coin name.
   */
  public static void getAllCoins(final Context context, final IProgressUpdateView<Integer> loaderView,
                                 final ITaskResultRecipient<Map<String, Coin>> resultRecipient) {
    if (allCoins == null || allCoins.isEmpty()) {
      ITaskResultRecipient<Map<String, Coin>> intermediateRecipient = new ITaskResultRecipient<Map<String, Coin>>() {
        @Override
        public void onTaskDone(Map<String, Coin> coinMap) {
          allCoins = coinMap;
          resultRecipient.onTaskDone(allCoins);
        }
      };
      GetCoinsTask getCoinsTask = new GetCoinsTask(loaderView, intermediateRecipient);
      try {
        getCoinsTask.execute(context.getResources().getString(R.string.coin_api_url));
      } catch (Exception e) {
        e.printStackTrace();
        loaderView.processingError(e.getMessage());
      }
    } else {
      resultRecipient.onTaskDone(allCoins);
    }
  }

  /**
   * Downloads images from requested URLs if not previously done, using a separate thread, and delivers the result to
   * specified ITaskResultRecipient. Note that ALL previously downloaded images also will be delivered, not only the
   * specific requests for this single method call.
   * @param urlMap
   * @param loaderView
   * @param resultRecipient
   */
  @SuppressWarnings("unchecked")
  public static void getCoinImages(Map<String, String> urlMap, final IProgressUpdateView<Integer> loaderView,
                                   final ITaskResultRecipient<Map<String, Bitmap>> resultRecipient) {
    Set<String> existingImages = new HashSet<String>(coinImages.keySet());
    Set<String> missingImages = new HashSet<String>(urlMap.keySet());
    // Find images that has not been downloaded before
    missingImages.removeAll(existingImages);
    // Start task if there are missing images
    if (!missingImages.isEmpty()) {
      Map.Entry<String, String>[] missingEntries = new Map.Entry[missingImages.size()];
      int i = 0;
      for (String coinName : missingImages) {
        missingEntries[i] = new AbstractMap.SimpleEntry<String, String>(coinName, urlMap.get(coinName));
        i++;
      }
      ITaskResultRecipient<Map<String, Bitmap>> intermediateRecipient = new ITaskResultRecipient<Map<String, Bitmap>>() {
        @Override
        public void onTaskDone(Map<String, Bitmap> imageMap) {
          coinImages.putAll(imageMap);
          resultRecipient.onTaskDone(coinImages);
        }
      };
      ImageDownloaderTask imageDownloaderTask = new ImageDownloaderTask(loaderView, intermediateRecipient);
      try {
        imageDownloaderTask.execute(missingEntries);
      } catch (Exception e) {
        e.printStackTrace();
        loaderView.processingError(e.getMessage());
      }
    } else {
      resultRecipient.onTaskDone(coinImages);
    }
  }

  /**
   * Fetches historic price data for specified coins, using a separate thread, and delivers the result to
   * specified ITaskResultRecipient. Does not cache results.
   * @param context
   * @param request
   * @param loaderView
   * @param resultRecipient
   */
  public static void getHistoricCoinPrices(Context context, HistoricPriceRequest request,
                                           final IProgressUpdateView<Integer> loaderView,
                                           final ITaskResultRecipient<Map<String, HistoricCoinPrice>> resultRecipient) {
    String url = context.getString(R.string.histo_price_api);
    HistoricCoinPriceTask histoTask = new HistoricCoinPriceTask(url, loaderView, resultRecipient);
    try {
      histoTask.execute(request);
    } catch (Exception e) {
      e.printStackTrace();
      loaderView.processingError(e.getMessage());
    }
  }
}

