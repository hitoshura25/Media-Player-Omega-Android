package com.vmenon.mpo;

import android.app.Application;
import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmenon.mpo.core.DownloadManager;
import com.vmenon.mpo.core.MPOExoPlayer;
import com.vmenon.mpo.core.MPOPlayer;
import com.vmenon.mpo.core.persistence.EpisodeDao;
import com.vmenon.mpo.core.persistence.MPORepository;
import com.vmenon.mpo.core.persistence.ShowDao;
import com.vmenon.mpo.core.persistence.MPODatabase;
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
    DownloadManager provideDownloadManager(MPORepository MPORepository) {
        return new DownloadManager(application.getApplicationContext(), MPORepository);
    }

    @Provides
    @Singleton
    MPODatabase provideMPODatabase() {
        return Room.databaseBuilder(application.getApplicationContext(), MPODatabase.class,
                "mpo-database").build();
    }

    @Provides
    @Singleton
    ShowDao provideShowDao(MPODatabase MPODatabase) {
        return MPODatabase.showDao();
    }

    @Provides
    @Singleton
    EpisodeDao provideEpisodeDao(MPODatabase MPODatabase) {
        return MPODatabase.episodeDao();
    }

    @Provides
    @Singleton
    MPORepository provideMPORepository(MediaPlayerOmegaService service,
                                           ShowDao showDao, EpisodeDao episodeDao) {
        return new MPORepository(service, showDao, episodeDao);
    }

    @Provides
    @Singleton
    MPOPlayer providePlayer() {
        return new MPOExoPlayer(application);
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
