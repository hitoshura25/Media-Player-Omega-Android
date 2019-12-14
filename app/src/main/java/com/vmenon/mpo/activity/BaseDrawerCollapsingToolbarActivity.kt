package com.vmenon.mpo.activity

import android.os.Bundle
import android.view.ViewStub

import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vmenon.mpo.R

abstract class BaseDrawerCollapsingToolbarActivity : BaseDrawerActivity(), AppBarLayout.OnOffsetChangedListener {

    private lateinit var fab: FloatingActionButton
    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private var collapsed = false
    private var scrollRange = -1

    protected abstract val fabDrawableResource: Int
    protected abstract val collapsedToolbarTitle: CharSequence
    protected abstract val expandedToolbarTitle: CharSequence
    protected abstract val collapsiblePanelContentLayoutId: Int

    override val rootLayoutResourceId: Int
        get() = R.layout.activity_base_drawer_collapsing_toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fab = findViewById(R.id.fab)
        fab.setImageResource(fabDrawableResource)
        fab.setOnClickListener { onFabClick() }

        collapsingToolbar = findViewById(R.id.collapsing_toolbar)
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar)
        appBarLayout.addOnOffsetChangedListener(this)
    }

    protected abstract fun onFabClick()

    override fun inflateContent() {
        val viewStub = findViewById<ViewStub>(R.id.contentViewStub)
        viewStub.layoutResource = layoutResourceId
        viewStub.inflate()

        val panelViewStub = findViewById<ViewStub>(R.id.collapsiblePanelViewStub)
        panelViewStub.layoutResource = collapsiblePanelContentLayoutId
        panelViewStub.inflate()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        if (scrollRange == -1) {
            scrollRange = appBarLayout.totalScrollRange
        }
        if (scrollRange + verticalOffset == 0) {
            collapsingToolbar.title = collapsedToolbarTitle
            collapsed = true
        } else if (collapsed) {
            collapsingToolbar.title = expandedToolbarTitle
            collapsed = false
        }
    }
}
