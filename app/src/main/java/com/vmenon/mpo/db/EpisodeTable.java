package com.vmenon.mpo.db;

public class EpisodeTable extends DbTable {
    public static final String TABLE_NAME = "episode";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ARTWORK_URL = "artwork_url";
    public static final String COLUMN_PUBLISHED = "published";
    public static final String COLUMN_FILE = "file";
    public static final String COLUMN_SHOW = "show_id";
    public static final String COLUMN_DOWNLOAD_URL = "download_url";

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_ARTWORK_URL + TEXT_TYPE + COMMA_SEP +
                    COLUMN_PUBLISHED + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_FILE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_DOWNLOAD_URL + TEXT_TYPE + COMMA_SEP +
                    COLUMN_SHOW + INTEGER_TYPE + " REFERENCES " + ShowTable.TABLE_NAME +
                    " )";
}
