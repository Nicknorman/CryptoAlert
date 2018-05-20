package com.nnk.cryptoalert.db.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tracked_coins")
public class TrackedCoin extends TrackedItem {

  public static TrackedCoin[] populateData() {
    TrackedCoin[] coins = new TrackedCoin[] {new TrackedCoin(), new TrackedCoin(), new TrackedCoin()};
    coins[0].name = "BTC";
    coins[1].name = "ETH";
    coins[2].name = "DOGE";
    return coins;
  }
}
