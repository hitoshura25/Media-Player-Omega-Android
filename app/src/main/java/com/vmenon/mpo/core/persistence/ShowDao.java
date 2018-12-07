package com.vmenon.mpo.core.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.vmenon.mpo.api.Show;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ShowDao {

    @Query("SELECT * FROM show where id = :id")
    Show getById(long id);

    @Query("SELECT * FROM show where id = :id")
    LiveData<Show> getLiveById(long id);

    @Insert(onConflict = REPLACE)
    void save(Show show);

    @Query("SELECT * FROM show")
    LiveData<List<Show>> load();

    @Query("SELECT * FROM show WHERE lastUpdate < :comparisonTime")
    List<Show> loadLastUpdatedBefore(long comparisonTime);
}
