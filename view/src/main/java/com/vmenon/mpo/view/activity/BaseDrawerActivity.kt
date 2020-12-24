package com.vmenon.mpo.view.activity

import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.ViewGroup
import com.google.android.material.navigation.NavigationView
import com.vmenon.mpo.navigation.domain.NavigationParams

import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.view.DrawerNavigationDestination
import com.vmenon.mpo.view.DrawerNavigationParams
import com.vmenon.mpo.view.DrawerNavigationRequest
import com.vmenon.mpo.view.R

abstract class BaseDrawerActivity<COMPONENT : Any, PARAMS : NavigationParams> :
    BaseActivity<COMPONENT>(), NavigationOrigin<PARAMS> {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    protected abstract val layoutResourceId: Int
    protected abstract val navMenuId: Int

    protected open val rootLayoutResourceId: Int
        get() = R.layout.activity_base_drawer

    protected open val isRootActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(rootLayoutResourceId)
        inflateContent()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)

        if (isRootActivity) {
            ab?.setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val location = when (menuItem.itemId) {
                R.id.nav_downloads -> DrawerNavigationDestination(R.id.nav_downloads)
                R.id.nav_library -> DrawerNavigationDestination(R.id.nav_library)
                else -> DrawerNavigationDestination(R.id.nav_home)
            }
            navigationController.onNavigationSelected(
                DrawerNavigationRequest(
                    location,
                    DrawerNavigationParams()
                ), this
            )
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }

    override fun onStart() {
        super.onStart()
        navigationView.setCheckedItem(navMenuId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected open fun inflateContent() {
        val contentView = findViewById<ViewGroup>(R.id.contentView)
        layoutInflater.inflate(layoutResourceId, contentView, true)
    }
}
