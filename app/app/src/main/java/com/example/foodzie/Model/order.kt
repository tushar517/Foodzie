package com.example.foodzie.Model

import org.json.JSONArray

data class order(
    val order_id:String,
    val res_name:String,
    val amount:String,
    val order_on:String,
    val food: JSONArray
)
