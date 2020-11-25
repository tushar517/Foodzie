package com.example.foodzie.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [RestrauntEntity::class],version = 1)
abstract class RestrauntDatabase:RoomDatabase() {
    abstract fun resDAO():RestrauntDAO
}

