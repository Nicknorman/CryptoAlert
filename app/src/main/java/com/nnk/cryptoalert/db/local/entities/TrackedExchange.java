package com.nnk.cryptoalert.db.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tracked_exchanges")
public class TrackedExchange extends TrackedItem {

  public static TrackedExchange[] populateData() {
    TrackedExchange[] exchanges = new TrackedExchange[] {new TrackedExchange()};
    exchanges[0].name ="Binance";
    return exchanges;
  }
}
