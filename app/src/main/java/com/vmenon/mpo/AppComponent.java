package com.vmenon.mpo;

import com.vmenon.mpo.activity.DownloadsActivity;
import com.vmenon.mpo.activity.LibraryActivity;
import com.vmenon.mpo.activity.MainActivity;
import com.vmenon.mpo.activity.ShowDetailsActivity;
import com.vmenon.mpo.activity.ShowSearchResultsActivity;
import com.vmenon.mpo.core.BackgroundService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(BackgroundService backgroundService);
    void inject(DownloadsActivity activity);
    void inject(LibraryActivity activity);
    void inject(MainActivity activity);
    void inject(ShowDetailsActivity activity);
    void inject(ShowSearchResultsActivity activity);
}
