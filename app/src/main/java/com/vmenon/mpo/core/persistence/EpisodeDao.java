package com.vmenon.mpo.core.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

import com.vmenon.mpo.api.Episode;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface EpisodeDao {
    @Insert(onConflict = REPLACE)
    void save(Episode episode);
}
