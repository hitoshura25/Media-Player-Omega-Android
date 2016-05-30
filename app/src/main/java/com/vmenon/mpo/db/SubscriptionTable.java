package com.vmenon.mpo.db;

public abstract class SubscriptionTable extends DbTable {
    public static final String TABLE_NAME = "subscription";
    public static final String COLUMN_SHOW_NAME = "show_name";
    public static final String COLUMN_FEED_URL = "feed_url";
    public static final String COLUMN_ARTWORK_URL = "artwork_url";
    public static final String COLUMN_LAST_EPISODE = "last_episode";
    public static final String COLUMN_LAST_UPDATE = "last_update";
    public static final String COLUMN_LAST_EPISODE_PUBLISHED = "last_episode_published";

    public static final String SQL_CREATE =
            "CREATE TABLE " + SubscriptionTable.TABLE_NAME + " (" +
                    SubscriptionTable._ID + " INTEGER PRIMARY KEY," +
                    SubscriptionTable.COLUMN_SHOW_NAME + TEXT_TYPE + COMMA_SEP +
                    SubscriptionTable.COLUMN_FEED_URL + TEXT_TYPE + COMMA_SEP +
                    SubscriptionTable.COLUMN_ARTWORK_URL + TEXT_TYPE + COMMA_SEP +
                    SubscriptionTable.COLUMN_LAST_EPISODE + TEXT_TYPE + COMMA_SEP +
                    SubscriptionTable.COLUMN_LAST_UPDATE + INTEGER_TYPE + COMMA_SEP +
                    SubscriptionTable.COLUMN_LAST_EPISODE_PUBLISHED + INTEGER_TYPE +
                    " )";
}
