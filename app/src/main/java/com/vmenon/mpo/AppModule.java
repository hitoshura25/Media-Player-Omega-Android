package com.vmenon.mpo;

import android.app.Application;

import com.vmenon.mpo.core.DownloadManager;
import com.vmenon.mpo.core.EventBus;
import com.vmenon.mpo.service.MediaPlayerOmegaService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {
    Application application;
    EventBus eventBus;

    public AppModule(Application application) {
        this.application = application;
        this.eventBus = new EventBus();
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return application;
    }

    @Provides
    @Singleton
    MediaPlayerOmegaService provideService() {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.API_URL)
                .build();

        return retrofit.create(MediaPlayerOmegaService.class);
    }

    @Provides
    @Singleton
    DownloadManager provideDownloadManager() {
        return new DownloadManager(application.getApplicationContext(), eventBus);
    }

    @Provides
    @Singleton
    EventBus provideEventBus() {
        return eventBus;
    }
}
