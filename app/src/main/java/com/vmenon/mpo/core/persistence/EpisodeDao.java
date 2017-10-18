package com.vmenon.mpo.core.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.vmenon.mpo.api.Episode;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

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
