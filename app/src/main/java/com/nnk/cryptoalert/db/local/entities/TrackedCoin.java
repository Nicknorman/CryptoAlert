package com.nnk.cryptoalert.db.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tracked_coins")
public class TrackedCoin extends TrackedItem {
  @PrimaryKey
  public int id;
}
