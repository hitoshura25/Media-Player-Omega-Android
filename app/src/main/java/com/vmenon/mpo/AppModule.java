package com.vmenon.mpo;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmenon.mpo.core.DownloadManager;
import com.vmenon.mpo.core.persistence.EpisodeDao;
import com.vmenon.mpo.core.persistence.PodcastDao;
import com.vmenon.mpo.core.persistence.PodcastDatabase;
import com.vmenon.mpo.core.persistence.PodcastRepository;
import com.vmenon.mpo.service.MediaPlayerOmegaService;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {
    Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return application;
    }

    @Provides
    @Singleton
    OkHttpClient provideHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    MediaPlayerOmegaService provideService(OkHttpClient httpClient) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(Constants.API_URL)
                .client(httpClient)
                .build();

        return retrofit.create(MediaPlayerOmegaService.class);
    }

    @Provides
    @Singleton
    DownloadManager provideDownloadManager(PodcastRepository podcastRepository) {
        return new DownloadManager(application.getApplicationContext(), podcastRepository);
    }

    @Provides
    @Singleton
    PodcastDatabase providePodcastDatabase() {
        return Room.databaseBuilder(application.getApplicationContext(), PodcastDatabase.class,
                "podcast-database").build();
    }

    @Provides
    @Singleton
    PodcastDao providePodcastDao(PodcastDatabase podcastDatabase) {
        return podcastDatabase.podcastDao();
    }

    @Provides
    @Singleton
    EpisodeDao provideEpisodeDao(PodcastDatabase podcastDatabase) {
        return podcastDatabase.episodeDao();
    }

    @Provides
    @Singleton
    PodcastRepository providePodcastRepository(MediaPlayerOmegaService service,
                                               PodcastDao podcastDao, EpisodeDao episodeDao) {
        return new PodcastRepository(service, podcastDao, episodeDao);
    }

    /**
     * TODO: Make sure MPO API doesn't return 0 byte responses for results...change
     * to just have an empty array, etc.
     **/
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
