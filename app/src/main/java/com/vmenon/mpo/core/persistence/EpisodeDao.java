package com.vmenon.mpo.core.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.vmenon.mpo.api.Episode;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface EpisodeDao {
    @Insert(onConflict = REPLACE)
    void save(Episode episode);

    @Query("SELECT * FROM episode")
    LiveData<List<Episode>> load();

    @Query("SELECT * from episode WHERE id = :id")
    Episode byId(long id);

    @Query("SELECT * from episode WHERE id = :id")
    LiveData<Episode> liveById(long id);
}
