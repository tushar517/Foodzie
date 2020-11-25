package com.example.foodzie.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
@Dao
interface MenuDAO {
    @Insert
    fun insertmenu(menuEntity: MenuEntity)

    @Delete
    fun deletemenu(menuEntity: MenuEntity)

    @Query("SELECT * FROM Menus")
    fun selectallmenu():List<MenuEntity>

    @Query("SELECT * FROM Menus WHERE menu_id=:menu_id")
    fun selectbyid(menu_id:String):MenuEntity

    @Query("SELECT COUNT() FROM Menus ")
    fun count():Int
    @Query("DELETE FROM Menus")
    fun deleteall()

}