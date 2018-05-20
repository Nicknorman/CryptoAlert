package com.nnk.cryptoalert.tasks;

/**
 * Created by Nick on 20/05/2018.
 */
public class HistoricPriceRequest {

  public enum HistoryType { DAY, HOUR, MINUTE }

  private HistoryType type;
  private int period;

  public int getAggregate() {
    return aggregate;
  }

  public void setAggregate(int aggregate) {
    this.aggregate = aggregate;
  }

  private int aggregate;
  private String[] coinNames;
  private String[] exchangeNames;
  private String[] toCoins;

  public HistoricPriceRequest(HistoryType type, int period, int aggregate, String[] coinNames, String[] exchangeNames, String[] toCoins) {
    this.type = type;
    this.period = period;
    this.aggregate = aggregate;
    this.coinNames = coinNames;
    this.exchangeNames = exchangeNames;
    this.toCoins = toCoins;
  }

  public HistoryType getType() {
    return type;
  }

  public void setType(HistoryType type) {
    this.type = type;
  }

  public int getPeriod() {
    return period;
  }

  public void setPeriod(int period) {
    this.period = period;
  }

  public String[] getCoinNames() {
    return coinNames;
  }

  public void setCoinNames(String[] coinNames) {
    this.coinNames = coinNames;
  }

  public String[] getExchangeNames() {
    return exchangeNames;
  }

  public void setExchangeNames(String[] exchangeNames) {
    this.exchangeNames = exchangeNames;
  }

  public String[] getToCoins() {
    return toCoins;
  }

  public void setToCoins(String[] toCoins) {
    this.toCoins = toCoins;
  }
}
