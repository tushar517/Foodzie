package com.example.foodzie.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Menus")
data class MenuEntity (
    @PrimaryKey val menu_id:String,
    @ColumnInfo(name = "menu_name")val name:String,
    @ColumnInfo(name = "cost_for_one")val cost:String,
    @ColumnInfo(name = "restraunt_id")val res_id:String
    )