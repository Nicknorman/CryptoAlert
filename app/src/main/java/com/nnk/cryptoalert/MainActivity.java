package com.nnk.cryptoalert;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

  private android.support.v4.app.FragmentManager fragmentManager;

  private CoinPriceHandlerFragment coinPriceHandler;
  private DrawerLayout drawerLayout;
  private Fragment currentUIFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    fragmentManager = getSupportFragmentManager();
    setupNavigationView();
    setupCoinPriceHandler();
  }

  private void setupCoinPriceHandler() {
    coinPriceHandler = new CoinPriceHandlerFragment();
    fragmentManager.beginTransaction().add(coinPriceHandler, "WORKER").commit();
  }

  private void setupNavigationView() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setHomeAsUpIndicator(R.drawable.ic_launcher_background);

    drawerLayout = findViewById(R.id.drawer_layout);
    // Setup navigation view
    NavigationView navigationView = findViewById(R.id.nav_view);
    final Activity main = this;
    navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            item.setChecked(true);
            drawerLayout.closeDrawers();

            int id = item.getItemId();
            switch (id) {
              case R.id.nav_tracking:
                currentUIFragment = new TrackingEditorFragment();
                break;
              default:
                return false;
            }
            fragmentManager.beginTransaction().replace(R.id.content_fragment, currentUIFragment)
                .addToBackStack(null).commit();
            return true;
          }
        }
    );
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
}
