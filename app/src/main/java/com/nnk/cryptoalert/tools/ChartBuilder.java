package com.nnk.cryptoalert.tools;

import android.content.Context;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.nnk.cryptoalert.R;
import com.nnk.cryptoalert.data.entities.HistoricPrice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 20/05/2018.
 */

public class ChartBuilder {

  /**
   * Creates a LineChart from specified data. Needs to be called on the UI thread.
   * @param data
   * @return
   */
  public static LineData buildHistoricalChartData(HistoricPrice[] data, Context context) {
    List<Entry> entries = new ArrayList<>();
    for (HistoricPrice priceEntry : data) {
      entries.add(new Entry(priceEntry.time, (float)priceEntry.price));
    }
    LineDataSet dataSet = new LineDataSet(entries, "Price");
    // Styling
    dataSet.setColor(context.getResources().getColor(R.color.chartData));
    dataSet.setLineWidth(3f);
    dataSet.setDrawValues(false);
    dataSet.setDrawCircles(false);
    LineData lineData = new LineData(dataSet);
    return lineData;
  }
}
