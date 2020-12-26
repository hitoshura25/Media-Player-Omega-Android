package com.vmenon.mpo.view.activity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.vmenon.mpo.MPOApplication
import com.vmenon.mpo.R
import com.vmenon.mpo.di.ActivityComponent
import com.vmenon.mpo.downloads.view.fragment.DownloadsFragment
import com.vmenon.mpo.library.view.fragment.LibraryFragment
import com.vmenon.mpo.library.view.fragment.SubscribedShowsFragment
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : BaseDrawerActivity<ActivityComponent, NoNavigationParams>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    override val navMenuId: Int
        get() = R.id.nav_home

    override val isRootActivity: Boolean
        get() = true

    override fun setupComponent(context: Context): ActivityComponent =
        (context as MPOApplication).appComponent.activityComponent().create()

    override fun inject(component: ActivityComponent) {
        component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewPager.adapter = FragmentAdapter(this)
        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_library -> viewPager.setCurrentItem(1, false)
                R.id.nav_downloads -> viewPager.setCurrentItem(2, false)
                else -> viewPager.setCurrentItem(0, false)
            }
            true
        }
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                navigation.selectedItemId = when (position) {
                    1 -> R.id.nav_library
                    2 -> R.id.nav_downloads
                    else -> R.id.nav_home
                }
            }
        })
        viewPager.isUserInputEnabled = false
    }

    class FragmentAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                1 -> LibraryFragment()
                2 -> DownloadsFragment()
                else -> SubscribedShowsFragment()
            }
        }
    }
}
