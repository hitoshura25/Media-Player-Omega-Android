package com.vmenon.mpo.api.di.dagger

import com.google.gson.GsonBuilder
import com.vmenon.mpo.api.MediaPlayerOmegaApi
import com.vmenon.mpo.api.retrofit.MediaPlayerOmegaRetrofitApi
import com.vmenon.mpo.api.retrofit.MediaPlayerOmegaRetrofitService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

@Module
object ApiModule {
    private val apiInstance: MediaPlayerOmegaApi by lazy {
        MediaPlayerOmegaRetrofitApi(provideService(provideHttpClient()))
    }

    @Provides
    fun provideApi(): MediaPlayerOmegaApi = apiInstance

    private fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private fun provideService(httpClient: OkHttpClient): MediaPlayerOmegaRetrofitService {
        val gson = GsonBuilder().create()
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("https://mpospboot.herokuapp.com/")
            .client(httpClient)
            .build()

        return retrofit.create(MediaPlayerOmegaRetrofitService::class.java)
    }

    /**
     * TODO: Make sure MPO API doesn't return 0 byte responses for results...change
     * to just have an empty array, etc.
     */
    private class NullOnEmptyConverterFactory : Converter.Factory() {
        override fun responseBodyConverter(
            type: Type, annotations: Array<Annotation>, retrofit: Retrofit
        ): Converter<ResponseBody, *>? {
            val delegate = retrofit.nextResponseBodyConverter<Any>(
                this, type, annotations
            )
            return Converter<ResponseBody, Any> { body ->
                if (body.contentLength() == 0L) {
                    null
                } else delegate.convert(body)
            }
        }
    }
}