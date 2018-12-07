package com.vmenon.mpo.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.google.android.material.navigation.NavigationView;
import com.vmenon.mpo.R;

public abstract class BaseDrawerActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getRootLayoutResourceId());

        inflateContent();

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        if (isRootActivity()) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

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

    protected int getRootLayoutResourceId() {
        return R.layout.activity_base_drawer;
    }

    protected void inflateContent() {
        ViewGroup contentView = findViewById(R.id.contentView);
        getLayoutInflater().inflate(getLayoutResourceId(), contentView, true);
    }

    protected boolean isRootActivity() {
        return false;
    }
}
