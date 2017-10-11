package com.vmenon.mpo.core.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.vmenon.mpo.api.Podcast;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface PodcastDao {
    @Insert(onConflict = REPLACE)
    void save(Podcast podcast);

    @Query("SELECT * FROM podcast")
    LiveData<List<Podcast>> load();

    @Query("SELECT * FROM podcast WHERE lastUpdate < :comparisonTime")
    List<Podcast> loadLastUpdatedBefore(long comparisonTime);
}
