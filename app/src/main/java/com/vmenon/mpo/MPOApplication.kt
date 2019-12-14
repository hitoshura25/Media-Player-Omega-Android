package com.vmenon.mpo

import android.app.Application

class MPOApplication : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        this.appComponent = DaggerAppComponent.builder().appModule(
                AppModule(this)).build()
    }

}
