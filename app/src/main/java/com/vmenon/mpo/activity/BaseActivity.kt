package com.vmenon.mpo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.vmenon.mpo.AppComponent
import com.vmenon.mpo.MPOApplication

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var appComponent: AppComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent = (application as MPOApplication).appComponent
    }
}
