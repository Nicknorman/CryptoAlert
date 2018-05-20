package com.nnk.cryptoalert.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.nnk.cryptoalert.R;
import com.nnk.cryptoalert.data.entities.CoinPriceEntry;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nick on 19/05/2018.
 */

public class CoinPriceAdapter extends RecyclerView.Adapter<CoinPriceAdapter.ViewHolder> {

  private Context context;
  private ArrayList<CoinPriceEntry> entries;

  /**
   * Adapter that shows a list of specified trackedCoins.
   * @param context
   * @param entries
   */
  public CoinPriceAdapter(Context context, ArrayList<CoinPriceEntry> entries) {
    this.context = context;
    this.entries = entries;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View coinPriceView = inflater.inflate(R.layout.entry_coin_price, parent, false);
    ViewHolder viewHolder = new ViewHolder(coinPriceView);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    CoinPriceEntry entry = entries.get(position);
    DecimalFormat decimalFormat = new DecimalFormat();
    holder.nameTextView.setText(entry.getName());
    if (entry.getPriceUSD() != null) {
      // Round to 5 decimal places
      decimalFormat.setMaximumFractionDigits(5);
      holder.priceTextView.setText("$" + decimalFormat.format(entry.getPriceUSD()));
    } else {
      holder.priceTextView.setText("");
    }
    if (entry.getChange() != null) {
      // Round to 2 decimal places
      decimalFormat.setMaximumFractionDigits(2);
      String roundedChange = decimalFormat.format(entry.getChange());
      if (entry.getChange() > 0) {
        holder.changeTextView.setText("+" + roundedChange.toString() + "%");
        holder.changeTextView.setTextColor(context.getResources().getColor(R.color.positive));
      } else if (entry.getChange() < 0) {
        holder.changeTextView.setText("-" + roundedChange.toString() + "%");
        holder.changeTextView.setTextColor(context.getResources().getColor(R.color.negative));
      } else {
        holder.changeTextView.setText("+" + roundedChange.toString() + "%");
        holder.changeTextView.setTextColor(context.getResources().getColor(R.color.neutral));
      }
    } else {
      holder.changeTextView.setText("");
    }
    if (entry.getImage() != null) {
      holder.coinImageView.setImageBitmap(entry.getImage());
    } else {
      holder.coinImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_default_coin_image));
    }
    if (entry.getChartData() != null) {
      holder.coinPriceChartView.setData(entry.getChartData());
      holder.coinPriceChartView.invalidate();
      holder.coinPriceChartView.notifyDataSetChanged();
    }
  }

  /**
   * Updates UI to correspond with new entries.
   * @param entries
   */
  public void updateEntries(ArrayList<CoinPriceEntry> entries) {
    this.entries = entries;
    notifyDataSetChanged();
  }

  public ArrayList<CoinPriceEntry> getEntries() {
    return entries;
  }

  @Override
  public int getItemCount() {
    return entries.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    private static SimpleDateFormat xAxisDateFormatter = new SimpleDateFormat("dd/MM");
    private static DecimalFormat decimalFormat;

    TextView nameTextView;
    TextView priceTextView;
    TextView changeTextView;
    ImageView coinImageView;
    LineChart coinPriceChartView;

    ViewHolder(View itemView) {
      super(itemView);
      nameTextView = itemView.findViewById(R.id.coin_name_text_view);
      priceTextView = itemView.findViewById(R.id.coin_price_text_view);
      changeTextView = itemView.findViewById(R.id.coin_change_text_view);
      coinImageView = itemView.findViewById(R.id.coin_image_image_view);
      coinPriceChartView = itemView.findViewById(R.id.coin_price_chart);
      if (decimalFormat == null) {
        decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
      }
      // Style graph
      coinPriceChartView.setAutoScaleMinMaxEnabled(true);
      coinPriceChartView.getDescription().setEnabled(false);
      coinPriceChartView.setDrawBorders(true);
      coinPriceChartView.getLegend().setEnabled(false);
      coinPriceChartView.setNoDataText("No data found.");
      XAxis xAxis = coinPriceChartView.getXAxis();
      xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
      xAxis.setLabelCount(2, true);
      xAxis.setValueFormatter(new IAxisValueFormatter() {
        @Override
        public String getFormattedValue(float millis, AxisBase axis) {
          return xAxisDateFormatter.format(new Date((long)millis*1000));
        }
      });
      YAxis leftAxis = coinPriceChartView.getAxisLeft();
      leftAxis.setLabelCount(2, true);
      leftAxis.setSpaceTop(0);
      leftAxis.setValueFormatter(new IAxisValueFormatter() {
        @Override
        public String getFormattedValue(float price, AxisBase axis) {
          return "$" + decimalFormat.format(price);
        }
      });
      YAxis rightAxis = coinPriceChartView.getAxisRight();
      rightAxis.setEnabled(false);
    }
  }
}

