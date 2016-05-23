package com.vmenon.mpo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.vmenon.mpo.R;
import com.vmenon.mpo.api.Podcast;
import com.vmenon.mpo.service.MediaPlayerOmegaService;
import com.vmenon.mpo.service.ServiceFactory;

import java.util.Collection;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private MediaPlayerOmegaService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        service = ServiceFactory.newInstance();

        View addPodcastButton = findViewById(R.id.addPodcastButton);
        addPodcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddPodcast();
            }
        });
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
    }

    public void onAddPodcast() {
        service.searchPodcasts("polygon")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Collection<Podcast>>() {
                    @Override
                    public final void onCompleted() {
                        // do nothing
                    }

                    @Override
                    public final void onError(Throwable e) {
                        Log.e("Error getting podcasts", e.getMessage());
                    }

                    @Override
                    public final void onNext(Collection<Podcast> response) {

                    }
                });
    }
}
