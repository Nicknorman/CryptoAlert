package com.nnk.cryptoalert.data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 24-03-2018.
 */

public class CoinPriceHistory {
  private int id;
  private Map<String, List<CoinPrice>> priceHistory;

  public CoinPriceHistory(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public Map<String, List<CoinPrice>> getPriceHistory() {
    return priceHistory;
  }

  public void setPriceHistory(Map<String, List<CoinPrice>> priceHistory) {
    this.priceHistory = priceHistory;
  }

  public class CoinPrice {
    private Date time;
    private double price;

    public Date getTime() {
      return time;
    }

    public void setTime(Date time) {
      this.time = time;
    }

    public double getPrice() {
      return price;
    }

    public void setPrice(double price) {
      this.price = price;
    }
  }
}
