package com.nnk.cryptoalert.tasks;

import com.nnk.cryptoalert.data.entities.Coin;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by Nick on 18/05/2018.
 */

public class GetCoinsTask extends UpdaterTask<String, Integer, Map<String, Coin>> {

  public GetCoinsTask(IProgressUpdateView<Integer> viewToUpdate, ITaskResultRecipient<Map<String, Coin>> resultRecipient) {
    super(viewToUpdate, resultRecipient);
  }

  /**
   * Will only try URLs other than the first if the first fails.
   * @param urls
   * @return
   */
  @Override
  protected Map<String, Coin> doInBackground(String... urls) {
    final Map<String, Coin> coins = new HashMap();
    boolean success = false;

    for (int i = 0; !success && i < urls.length; i++) {
      HttpURLConnection conn = null;
      try {
        conn = (HttpURLConnection) new URL(urls[i]).openConnection();
        InputStream inStream = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          result.append(line);
        }
        Map<String, Coin> parsedCoins = parseCoins(result.toString());
        if (!parsedCoins.isEmpty()) {
          coins.putAll(parsedCoins);
          success = true;
        }
      } catch (IOException e) {
        e.printStackTrace();
        viewToUpdate.processingError(e.getMessage());
      } catch (JSONException e) {
        e.printStackTrace();
        viewToUpdate.processingError(e.getMessage());
      } finally {
        if (conn != null) {
          conn.disconnect();
        }
      }
    }

    return coins;
  }

  protected Map<String, Coin> parseCoins(String json) throws JSONException {
    Map<String, Coin> coins = new HashMap();
    JSONObject jsonObject = new JSONObject(json);
    JSONObject coinMap = jsonObject.getJSONObject("Data");
    Iterator<String> iterator = coinMap.keys();
    int count = 0;
    while (iterator.hasNext()) {
      try {
        JSONObject coinJson = coinMap.getJSONObject(iterator.next());
        Coin coin = new Coin(Integer.parseInt(coinJson.getString("Id")));
        coin.setName(coinJson.getString("Name"));
        coin.setCoinName(coinJson.optString("CoinName"));
        coin.setFullName(coinJson.optString("FullName"));
        coin.setAlgorithm(coinJson.optString("Algorithm"));
        coin.setProofType(coinJson.optString("ProofType"));
        coin.setSortOrder(Integer.parseInt(coinJson.optString("SortOrder")));
        coin.setImageUrl(jsonObject.getString("BaseImageUrl") + coinJson.optString("ImageUrl"));
        coins.put(coin.getName(), coin);
      } catch (JSONException e) {
        e.printStackTrace();
        viewToUpdate.processingError(e.getMessage());
      }
      count++;
      publishProgress(count, coinMap.length());
    }
    return coins;
  }
}
