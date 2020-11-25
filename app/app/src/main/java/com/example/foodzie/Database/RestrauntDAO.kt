package com.example.foodzie.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.foodzie.Model.Restraunt

@Dao
interface RestrauntDAO {

     @Insert
     fun insertres(restrauntEntity: RestrauntEntity)

    @Delete
    fun deleteres(restrauntEntity: RestrauntEntity)

    @Query("SELECT * FROM Restraunts")
    fun selectallres():List<RestrauntEntity>

    @Query("SELECT * FROM Restraunts WHERE res_id=:resid AND user_id=:uid")
    fun selectbyid(resid:String,uid:String?):RestrauntEntity

    @Query("SELECT COUNT() FROM Restraunts")
    fun count():Int


}