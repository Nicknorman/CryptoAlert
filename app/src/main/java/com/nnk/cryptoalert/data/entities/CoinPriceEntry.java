package com.nnk.cryptoalert.data.entities;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import com.github.mikephil.charting.data.LineData;

/**
 * Created by Nick on 20/05/2018.
 */

public class CoinPriceEntry implements Parcelable {
  private String name;
  private Double priceUSD;
  private Double change;
  private Bitmap image;
  private LineData chartData;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getPriceUSD() {
    return priceUSD;
  }

  public void setPriceUSD(Double priceUSD) {
    this.priceUSD = priceUSD;
  }

  public Double getChange() {
    return change;
  }

  public void setChange(Double change) {
    this.change = change;
  }

  public Bitmap getImage() {
    return image;
  }

  public void setImage(Bitmap image) {
    this.image = image;
  }

  public LineData getChartData() {
    return chartData;
  }

  public void setChartData(LineData chartData) {
    this.chartData = chartData;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
    dest.writeDouble(priceUSD);
    dest.writeDouble(change);
    dest.writeValue(image);
    dest.writeValue(chartData);
  }

  public static final Parcelable.Creator<CoinPriceEntry> CREATOR = new Parcelable.Creator<CoinPriceEntry>() {
    @Override
    public CoinPriceEntry createFromParcel(Parcel source) {
      CoinPriceEntry entry = new CoinPriceEntry();
      entry.setName(source.readString());
      entry.setPriceUSD(source.readDouble());
      entry.setChange(source.readDouble());
      entry.setImage((Bitmap)source.readValue(null));
      entry.setChartData((LineData)source.readValue(null));
      return entry;
    }

    @Override
    public CoinPriceEntry[] newArray(int size) {
      return new CoinPriceEntry[size];
    }
  };
}
