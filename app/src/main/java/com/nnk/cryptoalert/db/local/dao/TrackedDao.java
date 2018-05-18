package com.nnk.cryptoalert.db.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.nnk.cryptoalert.db.local.entities.TrackedCoin;
import com.nnk.cryptoalert.db.local.entities.TrackedExchange;

import java.util.List;

@Dao
public interface TrackedDao {
  @Query("SELECT * FROM tracked_coins")
  List<TrackedCoin> getTrackedCoins();

  @Insert
  void addTrackedCoin(TrackedCoin... coins);

  @Delete
  void deleteTrackedCoins(TrackedCoin... coins);

  @Query("SELECT * FROM tracked_exchanges")
  List<TrackedExchange> getTrackedExchanges();

  @Insert
  void addTrackedExchange(TrackedExchange... exchanges);

  @Delete
  void deleteTrackedExchanges(TrackedExchange... exchanges);
}
