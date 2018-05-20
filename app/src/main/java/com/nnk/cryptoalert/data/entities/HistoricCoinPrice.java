package com.nnk.cryptoalert.data.entities;

import java.util.Map;

/**
 * Created by Nick on 20/05/2018.
 */

public class HistoricCoinPrice {

  /**
   * (fromCoin, value)
   */
  private Map<String, Double> currentPrice;
  private Map<String, HistoricPrice[]> historicPrices;

  public Map<String, Double> getCurrentPrice() {
    return currentPrice;
  }

  public void setCurrentPrice(Map<String, Double> currentPrice) {
    this.currentPrice = currentPrice;
  }

  public Map<String, HistoricPrice[]> getHistoricPrices() {
    return historicPrices;
  }

  public void setHistoricPrices(Map<String, HistoricPrice[]> historicPrices) {
    this.historicPrices = historicPrices;
  }
}
