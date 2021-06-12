package com.vmenon.mpo.persistence.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE downloads ADD COLUMN downloadAttempt INTEGER DEFAULT 0 NOT NULL")
        }
    }
}