package com.vmenon.mpo;

import com.vmenon.mpo.activity.DownloadsActivity;
import com.vmenon.mpo.activity.MainActivity;
import com.vmenon.mpo.activity.PodcastDetailsActivity;
import com.vmenon.mpo.activity.PodcastSearchResultsActivity;
import com.vmenon.mpo.core.BackgroundService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(BackgroundService backgroundService);
    void inject(DownloadsActivity activity);
    void inject(MainActivity activity);
    void inject(PodcastDetailsActivity activity);
    void inject(PodcastSearchResultsActivity activity);
}
