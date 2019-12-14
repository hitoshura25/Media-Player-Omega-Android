package com.vmenon.mpo

import android.app.Application
import androidx.room.Room

import com.google.gson.GsonBuilder
import com.vmenon.mpo.core.DownloadManager
import com.vmenon.mpo.core.MPOExoPlayer
import com.vmenon.mpo.core.MPOPlayer
import com.vmenon.mpo.core.persistence.EpisodeDao
import com.vmenon.mpo.core.persistence.MPORepository
import com.vmenon.mpo.core.persistence.ShowDao
import com.vmenon.mpo.core.persistence.MPODatabase
import com.vmenon.mpo.service.MediaPlayerOmegaService

import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class AppModule(internal var application: Application) {

    @Provides
    @Singleton
    internal fun providesApplication(): Application {
        return application
    }

    @Provides
    @Singleton
    internal fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    internal fun provideService(httpClient: OkHttpClient): MediaPlayerOmegaService {
        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Constants.API_URL)
            .client(httpClient)
            .build()

        return retrofit.create(MediaPlayerOmegaService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideDownloadManager(MPORepository: MPORepository): DownloadManager {
        return DownloadManager(application.applicationContext, MPORepository)
    }

    @Provides
    @Singleton
    internal fun provideMPODatabase(): MPODatabase {
        return Room.databaseBuilder(
            application.applicationContext, MPODatabase::class.java,
            "mpo-database"
        ).build()
    }

    @Provides
    @Singleton
    internal fun provideShowDao(MPODatabase: MPODatabase): ShowDao {
        return MPODatabase.showDao()
    }

    @Provides
    @Singleton
    internal fun provideEpisodeDao(MPODatabase: MPODatabase): EpisodeDao {
        return MPODatabase.episodeDao()
    }

    @Provides
    @Singleton
    internal fun provideMPORepository(
        service: MediaPlayerOmegaService,
        showDao: ShowDao, episodeDao: EpisodeDao
    ): MPORepository {
        return MPORepository(service, showDao, episodeDao)
    }

    @Provides
    @Singleton
    internal fun providePlayer(): MPOPlayer {
        return MPOExoPlayer(application)
    }

    /**
     * TODO: Make sure MPO API doesn't return 0 byte responses for results...change
     * to just have an empty array, etc.
     */
    internal inner class NullOnEmptyConverterFactory : Converter.Factory() {
        override fun responseBodyConverter(
            type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?
        ): Converter<ResponseBody, *>? {
            val delegate = retrofit!!.nextResponseBodyConverter<Any>(
                this, type!!, annotations!!
            )
            return Converter<ResponseBody, Any> { body ->
                if (body.contentLength() == 0L) {
                    null
                } else delegate.convert(body)
            }
        }
    }
}
