package com.vmenon.mpo;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmenon.mpo.core.DownloadManager;
import com.vmenon.mpo.core.EventBus;
import com.vmenon.mpo.core.SubscriptionDao;
import com.vmenon.mpo.db.DbHelper;
import com.vmenon.mpo.service.MediaPlayerOmegaService;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {
    Application application;
    EventBus eventBus;
    SubscriptionDao subscriptionDao;

    public AppModule(Application application) {
        this.application = application;
        this.eventBus = new EventBus();
        this.subscriptionDao = new SubscriptionDao(
                new DbHelper(application.getApplicationContext()));
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return application;
    }

    @Provides
    @Singleton
    MediaPlayerOmegaService provideService() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();


        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(Constants.API_URL)
                .build();

        return retrofit.create(MediaPlayerOmegaService.class);
    }

    @Provides
    @Singleton
    DownloadManager provideDownloadManager() {
        return new DownloadManager(application.getApplicationContext(), eventBus, subscriptionDao);
    }

    @Provides
    @Singleton
    EventBus provideEventBus() {
        return eventBus;
    }

    @Provides
    @Singleton
    SubscriptionDao provideSubscriptionDao() {
        return new SubscriptionDao(new DbHelper(application.getApplicationContext()));
    }

    class NullOnEmptyConverterFactory extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(
                Type type, Annotation[] annotations, Retrofit retrofit) {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(
                    this, type, annotations);
            return new Converter<ResponseBody, Object>() {
                @Override
                public Object convert(ResponseBody body) throws IOException {
                    if (body.contentLength() == 0) {
                        return null;
                    }
                    return delegate.convert(body);
                }
            };
        }
    }
}
