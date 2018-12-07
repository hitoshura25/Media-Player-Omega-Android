package com.vmenon.mpo.core.persistence;

import androidx.room.*;

import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Show;

@Database(entities = {Show.class, Episode.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class MPODatabase extends RoomDatabase {
    public abstract ShowDao showDao();
    public abstract EpisodeDao episodeDao();
}
