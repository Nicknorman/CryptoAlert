package com.nnk.cryptoalert.db.local;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import android.support.annotation.NonNull;
import com.nnk.cryptoalert.db.local.dao.TrackedDao;
import com.nnk.cryptoalert.db.local.entities.TrackedCoin;
import com.nnk.cryptoalert.db.local.entities.TrackedExchange;

import java.util.concurrent.Executors;

@Database(entities = {TrackedCoin.class, TrackedExchange.class}, version = 1, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {

  private static LocalDatabase INSTANCE;
  public abstract TrackedDao trackedDao();

  public static LocalDatabase getInstance(final Context context) {
    if (INSTANCE == null) {
      INSTANCE = Room.databaseBuilder(
          context.getApplicationContext(),
          LocalDatabase.class,
          "tracked-database").addCallback(new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
          super.onCreate(db);
          // Pre-populate db on first run
          Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
              getInstance(context).trackedDao().addTrackedCoin(TrackedCoin.populateData());
              getInstance(context).trackedDao().addTrackedExchange(TrackedExchange.populateData());
            }
          });
        }
      }).build();
    }
    return INSTANCE;
  }

  public static void destroyInstance() {
    INSTANCE = null;
  }
}
