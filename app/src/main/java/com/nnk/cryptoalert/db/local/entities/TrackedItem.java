package com.nnk.cryptoalert.db.local.entities;

import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

public class TrackedItem {
  @PrimaryKey
  @NonNull
  public String name;
}
