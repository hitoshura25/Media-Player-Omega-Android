package com.vmenon.mpo.view.activity

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.widget.SearchView

import android.view.Menu
import com.vmenon.mpo.MPOApplication

import com.vmenon.mpo.R
import com.vmenon.mpo.di.ActivityComponent

class HomeActivity : BaseDrawerActivity<ActivityComponent>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    override val navMenuId: Int
        get() = R.id.nav_home

    override val isRootActivity: Boolean
        get() = true

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val menuItem = menu.findItem(R.id.search)
        val searchView = menuItem.actionView as SearchView
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(componentName)
        )

        return true
    }

    override fun setupComponent(context: Context): ActivityComponent =
        (context as MPOApplication).appComponent.activityComponent().create()

    override fun inject(component: ActivityComponent) {
        component.inject(this)
    }
}
