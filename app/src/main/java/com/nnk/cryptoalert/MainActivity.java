package com.nnk.cryptoalert;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.nnk.cryptoalert.db.local.LocalDatabase;
import com.nnk.cryptoalert.tasks.IProgressUpdateView;
import com.nnk.cryptoalert.fragments.CoinPriceOverviewFragment;
import com.nnk.cryptoalert.fragments.PausableFragment;
import com.nnk.cryptoalert.fragments.TrackingEditorFragment;

public class MainActivity extends AppCompatActivity implements IProgressUpdateView<Integer> {

  private PausableFragment currentUIFragment;

  private DrawerLayout drawerLayout;
  private FrameLayout progressBarFrame;
  private ProgressBar progressBar;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // Pre-populate db with coins and exchanges
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        LocalDatabase.getInstance(getApplicationContext()).trackedDao().getTrackedExchanges();
      }
    });
    progressBarFrame = this.findViewById(R.id.progress_bar_frame);
    progressBar = this.findViewById(R.id.progress_bar);
    setupNavigationView();
    setupFragments();
  }

  private void setupFragments() {
    currentUIFragment = new CoinPriceOverviewFragment();
    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, currentUIFragment).commit();
  }

  private void setupNavigationView() {
    drawerLayout = findViewById(R.id.drawer_layout);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
        R.string.drawer_open, R.string.drawer_close) {
      public void onDrawerClosed(View view) {
        super.onDrawerClosed(view);
      }

      public void onDrawerOpened(View view) {
        super.onDrawerOpened(view);
      }
    };
    drawerToggle.syncState();
    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    getSupportActionBar().setHomeButtonEnabled(true);

    // Setup navigation view
    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            item.setChecked(true);
            switch (id) {
              case R.id.nav_tracking_editor:
                currentUIFragment = new TrackingEditorFragment();
                break;
              case R.id.nav_coin_price_overview:
                currentUIFragment = new CoinPriceOverviewFragment();
                break;
              default:
                drawerLayout.closeDrawers();
                return false;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, currentUIFragment).commit();
            drawerLayout.closeDrawers();
            return true;
          }
        }
    );
    navigationView.bringToFront();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        drawerLayout.openDrawer(GravityCompat.START);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Disable current fragment's UI and show a progressBar.
   */
  @Override
  public void preProcess() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        currentUIFragment.setEnabled(false);
        progressBar.setProgress(0);
        progressBar.setEnabled(true);
        progressBarFrame.setVisibility(View.VISIBLE);
      }
    });
  }

  /**
   * Update progress on progressBar.
   * @param current
   * @param total
   */
  @Override
  public void updateProgress(Integer current, Integer total) {
    progressBar.setMax(total);
    progressBar.setProgress(current);
  }

  /**
   * Disable progressBar and enable current fragment's UI.
   */
  @Override
  public void processingDone() {
    progressBar.setEnabled(false);
    progressBarFrame.setVisibility(View.GONE);
    currentUIFragment.setEnabled(true);
  }

  /**
   * Shows error that happened while task was running, on UI thread.
   * @param message
   */
  @Override
  public void processingError(final String message) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
      }
    });
  }

  @Override
  protected void onSaveInstanceState(final Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(final Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
  }
}
