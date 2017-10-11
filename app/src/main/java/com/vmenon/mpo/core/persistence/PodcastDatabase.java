package com.vmenon.mpo.core.persistence;

import android.arch.persistence.room.*;

import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Podcast;

@Database(entities = {Podcast.class, Episode.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class PodcastDatabase extends RoomDatabase {
    public abstract PodcastDao podcastDao();
    public abstract EpisodeDao episodeDao();
}
