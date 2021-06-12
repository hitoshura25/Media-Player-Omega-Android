package com.vmenon.mpo.api.di.dagger

import com.google.gson.GsonBuilder
import com.vmenon.mpo.api.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.api.retrofit.OAuthInterceptor
import com.vmenon.mpo.api.retrofit.RetryInterceptor
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.login.domain.AuthService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import javax.inject.Named

@Module
object ApiModule {

    @Provides
    fun provideMediaPlayerRetrofitApi(
        @Named("mpoApiUrl") baseUrl: String,
        httpClient: OkHttpClient
    ): MediaPlayerOmegaRetrofitService = provideService(baseUrl, httpClient)

    @Provides
    @Named("mpoApiUrl")
    fun provideMpoApiUrl(): String = "https://mpospboot.herokuapp.com/" // "http://10.0.0.208:8080/"

    @Provides
    fun provideHttpClient(authService: AuthService, system: System): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(OAuthInterceptor(authService, system))
            .addInterceptor(RetryInterceptor(MAX_RETRIES, system))
            .build()
    }

    private fun provideService(
        baseUrl: String,
        httpClient: OkHttpClient
    ): MediaPlayerOmegaRetrofitService {
        val gson = GsonBuilder().create()
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
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

    private const val MAX_RETRIES = 2
}