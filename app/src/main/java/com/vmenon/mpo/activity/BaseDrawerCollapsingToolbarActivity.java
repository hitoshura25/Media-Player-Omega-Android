package com.vmenon.mpo.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vmenon.mpo.R;

public abstract class BaseDrawerCollapsingToolbarActivity extends BaseDrawerActivity
        implements AppBarLayout.OnOffsetChangedListener {

    private FloatingActionButton fab;
    private CollapsingToolbarLayout collapsingToolbar;
    private boolean collapsed = false;
    private int scrollRange = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fab = findViewById(R.id.fab);
        fab.setImageResource(getFabDrawableResource());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabClick();
            }
        });

        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        final AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);
    }

    protected abstract int getFabDrawableResource();
    protected abstract CharSequence getCollapsedToolbarTitle();
    protected abstract CharSequence getExpandedToolbarTitle();
    protected abstract void onFabClick();
    protected abstract int getCollapsiblePanelContentLayoutId();

    @Override
    protected int getRootLayoutResourceId() {
        return R.layout.activity_base_drawer_collapsing_toolbar;
    }

    @Override
    protected void inflateContent() {
        ViewStub viewStub = findViewById(R.id.contentViewStub);
        viewStub.setLayoutResource(getLayoutResourceId());
        viewStub.inflate();

        ViewStub panelViewStub = findViewById(R.id.collapsiblePanelViewStub);
        panelViewStub.setLayoutResource(getCollapsiblePanelContentLayoutId());
        panelViewStub.inflate();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
            scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
            collapsingToolbar.setTitle(getCollapsedToolbarTitle());
            collapsed = true;
        } else if (collapsed) {
            collapsingToolbar.setTitle(getExpandedToolbarTitle());
            collapsed = false;
        }
    }
}
