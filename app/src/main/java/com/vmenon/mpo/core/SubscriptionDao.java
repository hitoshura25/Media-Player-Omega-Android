package com.vmenon.mpo.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vmenon.mpo.api.Podcast;
import com.vmenon.mpo.db.DbHelper;
import com.vmenon.mpo.db.SubscriptionTable;

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
        values.put(SubscriptionTable.COLUMN_SHOW_NAME, podcast.name);
        values.put(SubscriptionTable.COLUMN_FEED_URL, podcast.feedUrl);
        values.put(SubscriptionTable.COLUMN_ARTWORK_URL, podcast.artworkUrl);
        values.put(SubscriptionTable.COLUMN_LAST_EPISODE, podcast.lastEpisode);
        values.put(SubscriptionTable.COLUMN_LAST_UPDATE, podcast.lastUpdate);

        if (podcast.id == -1L) {
            final long newRowId = db.insert(SubscriptionTable.TABLE_NAME, null, values);
            podcast.id = newRowId;
        } else {
            String selection = SubscriptionTable._ID + " = ?";
            String[] selectionArgs = { String.valueOf(podcast.id) };

            db.update(
                    SubscriptionTable.TABLE_NAME,
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
        String selection = SubscriptionTable.COLUMN_LAST_UPDATE + " < ?";
        String[] selectionArgs = { String.valueOf(compareTime) };

        return query(selection, selectionArgs);
    }

    private String[] getDefaultProject() {
        return new String[] {
                SubscriptionTable._ID,
                SubscriptionTable.COLUMN_SHOW_NAME,
                SubscriptionTable.COLUMN_FEED_URL,
                SubscriptionTable.COLUMN_ARTWORK_URL,
                SubscriptionTable.COLUMN_LAST_EPISODE,
                SubscriptionTable.COLUMN_LAST_UPDATE
        };
    }

    private List<Podcast> query(String selection, String[] selectionArgs) {
        final List<Podcast> podcasts = new ArrayList<>();
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final Cursor cursor = db.query(
                SubscriptionTable.TABLE_NAME, getDefaultProject(), selection, selectionArgs,
                null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Podcast podcast = new Podcast();
            podcast.id = cursor.getLong(cursor.getColumnIndexOrThrow(SubscriptionTable._ID));
            podcast.name = cursor.getString(cursor.getColumnIndexOrThrow(
                    SubscriptionTable.COLUMN_SHOW_NAME));
            podcast.feedUrl = cursor.getString(cursor.getColumnIndexOrThrow(
                    SubscriptionTable.COLUMN_FEED_URL));
            podcast.artworkUrl = cursor.getString(cursor.getColumnIndexOrThrow(
                    SubscriptionTable.COLUMN_ARTWORK_URL));
            podcast.lastEpisode = cursor.getString(cursor.getColumnIndexOrThrow(
                    SubscriptionTable.COLUMN_LAST_EPISODE));
            podcast.lastUpdate = cursor.getLong(cursor.getColumnIndexOrThrow(
                    SubscriptionTable.COLUMN_LAST_UPDATE));
            podcasts.add(podcast);
            cursor.moveToNext();
        }
        cursor.close();
        return podcasts;
    }
}
