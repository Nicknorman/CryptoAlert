package com.nnk.cryptoalert.db.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.nnk.cryptoalert.db.local.dao.TrackedDao;
import com.nnk.cryptoalert.db.local.entities.TrackedCoin;
import com.nnk.cryptoalert.db.local.entities.TrackedExchange;

@Database(entities = {TrackedCoin.class, TrackedExchange.class}, version = 1, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {

  private static LocalDatabase INSTANCE;
  public abstract TrackedDao trackedDao();

  public static LocalDatabase getLocalDatabase(Context context) {
    if (INSTANCE == null) {
      INSTANCE = Room.databaseBuilder(
          context.getApplicationContext(),
          LocalDatabase.class,
          "tracked-database")
          .build();
    }
    return INSTANCE;
  }

  public static void destroyInstance() {
    INSTANCE = null;
  }
}
