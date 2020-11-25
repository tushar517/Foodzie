package com.example.foodzie.Database

import android.view.Menu
import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase

@Database(entities = [MenuEntity::class],version = 1)
abstract class MenuDatabase:RoomDatabase() {
    abstract fun menudao():MenuDAO
}