package com.example.foodzie.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Restraunts")
data class RestrauntEntity (
    @PrimaryKey val res_id:Int,
    @ColumnInfo(name = "user_id") val user_id:String?,
    @ColumnInfo(name = "Restraunt_Name") val res_name:String,
    @ColumnInfo(name = "Restraunt_Rating") val res_rating:String,
    @ColumnInfo(name = "Restraunt_cost_per_person") val res_cost:String,
    @ColumnInfo(name = "Restraunt_Image") val res_img:String
)