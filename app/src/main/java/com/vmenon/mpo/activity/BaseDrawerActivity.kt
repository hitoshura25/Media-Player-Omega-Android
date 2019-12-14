package com.vmenon.mpo.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.ViewGroup

import com.google.android.material.navigation.NavigationView
import com.vmenon.mpo.R

abstract class BaseDrawerActivity : BaseActivity() {

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
            when {
                R.id.nav_downloads == menuItem.itemId -> {
                    val intent = Intent(this@BaseDrawerActivity, DownloadsActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_library == menuItem.itemId -> {
                    val intent = Intent(this@BaseDrawerActivity, LibraryActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_home == menuItem.itemId -> {
                    val intent = Intent(this@BaseDrawerActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }

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
