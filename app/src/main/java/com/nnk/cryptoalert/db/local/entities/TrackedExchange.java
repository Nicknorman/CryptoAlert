package com.nnk.cryptoalert.db.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tracked_exchanges")
public class TrackedExchange extends TrackedItem {
  @PrimaryKey
  public int id;
}
