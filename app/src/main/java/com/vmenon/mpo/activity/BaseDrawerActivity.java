package com.vmenon.mpo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.vmenon.mpo.R;

public abstract class BaseDrawerActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_drawer);
        ViewGroup contentView = findViewById(R.id.contentView);
        getLayoutInflater().inflate(getLayoutResourceId(), contentView, true);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (R.id.nav_downloads == menuItem.getItemId()) {
                            Intent intent = new Intent(BaseDrawerActivity.this, DownloadsActivity.class);
                            startActivity(intent);
                        } else if (R.id.nav_library == menuItem.getItemId()) {
                            Intent intent = new Intent(BaseDrawerActivity.this, LibraryActivity.class);
                            startActivity(intent);
                        } else if (R.id.nav_home == menuItem.getItemId()) {
                            Intent intent = new Intent(BaseDrawerActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationView.setCheckedItem(getNavMenuId());
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

    protected abstract int getLayoutResourceId();

    protected abstract int getNavMenuId();
}
