package com.vmenon.mpo.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Podcast;
import com.vmenon.mpo.db.DbHelper;
import com.vmenon.mpo.db.EpisodeTable;
import com.vmenon.mpo.db.ShowTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SubscriptionDao {
    private DbHelper dbHelper;
    public SubscriptionDao(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void save(Podcast podcast) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ShowTable.COLUMN_NAME, podcast.name);
        values.put(ShowTable.COLUMN_FEED_URL, podcast.feedUrl);
        values.put(ShowTable.COLUMN_ARTWORK_URL, podcast.artworkUrl);
        values.put(ShowTable.COLUMN_LAST_UPDATE, podcast.lastUpdate);

        if (podcast.id == -1L) {
            final long newRowId = db.insert(ShowTable.TABLE_NAME, null, values);
            podcast.id = newRowId;
        } else {
            String selection = ShowTable._ID + " = ?";
            String[] selectionArgs = { String.valueOf(podcast.id) };

            db.update(
                    ShowTable.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
    }

    public void save(Episode episode) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EpisodeTable.COLUMN_NAME, episode.name);
        values.put(EpisodeTable.COLUMN_ARTWORK_URL, episode.artworkUrl);
        values.put(EpisodeTable.COLUMN_PUBLISHED, episode.published);
        values.put(EpisodeTable.COLUMN_SHOW, episode.showId);
        values.put(EpisodeTable.COLUMN_DOWNLOAD_URL, episode.downloadUrl);

        if (episode.id == -1L) {
            final long newRowId = db.insert(EpisodeTable.TABLE_NAME, null, values);
            episode.id = newRowId;
        } else {
            String selection = EpisodeTable._ID + " = ?";
            String[] selectionArgs = { String.valueOf(episode.id) };

            db.update(
                    EpisodeTable.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
    }

    public List<Podcast> all() {
        return query(null, null);
    }

    public List<Podcast> notUpdatedInLast(long milliseconds) {
        final long compareTime = new Date().getTime() - milliseconds;
        String selection = ShowTable.COLUMN_LAST_UPDATE + " < ?";
        String[] selectionArgs = { String.valueOf(compareTime) };

        return query(selection, selectionArgs);
    }

    public void updateLastPublishedEpisode(long podcastId, long episodeDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ShowTable.COLUMN_LAST_EPISODE_PUBLISHED, episodeDate);

        String selection = ShowTable._ID + " = ?" ;
        String[] selectionArgs = {
                String.valueOf(podcastId)
        };

        int count = db.update(
                ShowTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        Log.d("MPO", count + " rows updated with last published episode date: " + episodeDate);
    }

    private String[] getDefaultProject() {
        return new String[] {
                ShowTable._ID,
                ShowTable.COLUMN_NAME,
                ShowTable.COLUMN_FEED_URL,
                ShowTable.COLUMN_ARTWORK_URL,
                ShowTable.COLUMN_LAST_UPDATE,
                ShowTable.COLUMN_LAST_EPISODE_PUBLISHED
        };
    }

    private List<Podcast> query(String selection, String[] selectionArgs) {
        final List<Podcast> podcasts = new ArrayList<>();
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final Cursor cursor = db.query(
                ShowTable.TABLE_NAME, getDefaultProject(), selection, selectionArgs,
                null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Podcast podcast = new Podcast();
            podcast.id = cursor.getLong(cursor.getColumnIndexOrThrow(ShowTable._ID));
            podcast.name = cursor.getString(cursor.getColumnIndexOrThrow(
                    ShowTable.COLUMN_NAME));
            podcast.feedUrl = cursor.getString(cursor.getColumnIndexOrThrow(
                    ShowTable.COLUMN_FEED_URL));
            podcast.artworkUrl = cursor.getString(cursor.getColumnIndexOrThrow(
                    ShowTable.COLUMN_ARTWORK_URL));
            podcast.lastUpdate = cursor.getLong(cursor.getColumnIndexOrThrow(
                    ShowTable.COLUMN_LAST_UPDATE));
            podcast.lastEpisodePublished = cursor.getLong(cursor.getColumnIndexOrThrow(
                    ShowTable.COLUMN_LAST_EPISODE_PUBLISHED));
            podcasts.add(podcast);
            cursor.moveToNext();
        }
        cursor.close();
        return podcasts;
    }
}
