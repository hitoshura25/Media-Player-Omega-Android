package com.vmenon.mpo.persistence.di.dagger

import android.app.Application
import androidx.room.Room
import com.vmenon.mpo.persistence.room.MPODatabase
import dagger.Module
import dagger.Provides

@Module
object DatabaseModule {
    @Provides
    @PersistenceScope
    fun provideMPODatabase(application: Application): MPODatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            MPODatabase::class.java,
            "mpo-database"
        ).build()
    }
}