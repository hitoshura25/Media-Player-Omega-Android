package com.vmenon.mpo.db;

public class ShowTable extends DbTable {
    public static final String TABLE_NAME = "show";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_FEED_URL = "feed_url";
    public static final String COLUMN_ARTWORK_URL = "artwork_url";
    public static final String COLUMN_LAST_UPDATE = "last_update";
    public static final String COLUMN_LAST_EPISODE_PUBLISHED = "last_episode_published";
    public static final String COLUMN_SUBSCRIBED = "subscribed";

    public static final String SQL_CREATE =
            "CREATE TABLE " + ShowTable.TABLE_NAME + " (" +
                    ShowTable._ID + " INTEGER PRIMARY KEY," +
                    ShowTable.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    ShowTable.COLUMN_FEED_URL + TEXT_TYPE + COMMA_SEP +
                    ShowTable.COLUMN_ARTWORK_URL + TEXT_TYPE + COMMA_SEP +
                    ShowTable.COLUMN_LAST_UPDATE + INTEGER_TYPE + COMMA_SEP +
                    ShowTable.COLUMN_LAST_EPISODE_PUBLISHED + INTEGER_TYPE + COMMA_SEP +
                    ShowTable.COLUMN_SUBSCRIBED + INTEGER_TYPE +
                    " )";
}
