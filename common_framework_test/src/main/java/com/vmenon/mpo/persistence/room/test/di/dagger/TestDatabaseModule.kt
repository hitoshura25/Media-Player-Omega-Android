package com.vmenon.mpo.persistence.room.test.di.dagger

import android.app.Application
import androidx.room.Room
import com.vmenon.mpo.persistence.di.dagger.PersistenceScope
import com.vmenon.mpo.persistence.room.MPODatabase
import dagger.Module
import dagger.Provides

@Module
object TestDatabaseModule {
    @Provides
    @PersistenceScope
    fun provideMPODatabase(application: Application): MPODatabase {
        return Room.inMemoryDatabaseBuilder(
            application.applicationContext,
            MPODatabase::class.java
        ).build()
    }
}