package com.vmenon.mpo.core.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.vmenon.mpo.api.Show;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ShowDao {
    @Insert(onConflict = REPLACE)
    void save(Show show);

    @Query("SELECT * FROM show")
    LiveData<List<Show>> load();

    @Query("SELECT * FROM show WHERE lastUpdate < :comparisonTime")
    List<Show> loadLastUpdatedBefore(long comparisonTime);
}
