package com.nnk.cryptoalert.tasks;

import com.nnk.cryptoalert.data.entities.HistoricCoinPrice;
import com.nnk.cryptoalert.data.entities.HistoricPrice;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 20/05/2018.
 */

public class HistoricCoinPriceTask extends
    UpdaterTask<HistoricPriceRequest, Integer, Map<String, HistoricCoinPrice>> {

  private String url;

  public HistoricCoinPriceTask(String url, IProgressUpdateView<Integer> viewToUpdate,
                               ITaskResultRecipient<Map<String, HistoricCoinPrice>> resultRecipient) {
    super(viewToUpdate, resultRecipient);
    this.url = url;
  }

  @Override
  protected Map<String, HistoricCoinPrice> doInBackground(HistoricPriceRequest... historicPriceRequests) {
    HistoricPriceRequest request = historicPriceRequests[0];
    Map<String, HistoricCoinPrice> histoCoinPrices = new HashMap<>();
    String histType = request.getType().name().toLowerCase();

    for (int i = 0; i < request.getCoinNames().length; i++) {
      HttpURLConnection conn = null;
      String coinName = request.getCoinNames()[i];
      // Format URL with parameters
      String formattedUrl = String.format(url, histType, coinName, request.getToCoins()[0], request.getAggregate(),
          request.getPeriod());
      try {
        conn = (HttpURLConnection) new URL(formattedUrl).openConnection();
        InputStream inStream = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          result.append(line);
        }
        // Create histo object with parsed data
        HistoricCoinPrice histoData = new HistoricCoinPrice();
        Map<String, HistoricPrice[]> histoPricesMap = new HashMap<>();
        HistoricPrice[] histoPriceArray = parseHistoData(result.toString());
        histoPricesMap.put(request.getToCoins()[0], histoPriceArray);
        histoData.setHistoricPrices(histoPricesMap);
        Map<String, Double> currentPrice = new HashMap<>();
        if (histoPriceArray.length > 0) {
          currentPrice.put(request.getToCoins()[0], histoPriceArray[histoPriceArray.length - 1].price);
        }
        histoData.setCurrentPrice(currentPrice);
        histoCoinPrices.put(coinName, histoData);
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
      publishProgress(i, request.getCoinNames().length);
    }

    return histoCoinPrices;
  }

  private HistoricPrice[] parseHistoData(String json) throws JSONException {
    JSONObject rootObject = new JSONObject(json);
    JSONArray dataArray = rootObject.getJSONArray("Data");
    HistoricPrice[] histoPrices = new HistoricPrice[dataArray.length()];
    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject dataObject = dataArray.getJSONObject(i);
      histoPrices[i] = new HistoricPrice();
      histoPrices[i].price = dataObject.getDouble("close");
      histoPrices[i].time = dataObject.getLong("time");
    }
    return histoPrices;
  }
}
