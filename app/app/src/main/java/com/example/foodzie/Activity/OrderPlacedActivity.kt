package com.example.foodzie.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodzie.Database.MenuDatabase
import com.example.foodzie.Database.MenuEntity
import com.example.foodzie.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class OrderPlacedActivity : AppCompatActivity() {
    lateinit var orderid: String
    lateinit var sharedPreferences: SharedPreferences
    var order = arrayListOf<MenuEntity>()
    val foodid = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        Handler().postDelayed({
        sharedPreferences =getSharedPreferences(getString(R.string.preference_file_name), MODE_PRIVATE)
        orderid = intent.getStringExtra("payment_id")
        val uid = sharedPreferences.getString("user_id", "")
        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"
        var paramas = HashMap<String, String>()
        val res_id = intent.getStringExtra("restraunt_id")
        val total_cost = intent.getStringExtra("bill")
        order = AsyncOrderlistDB(this).execute().get() as ArrayList<MenuEntity>
        for (i in order) {
            val obj = JSONObject()
            obj.put("food_item_id", i.menu_id)
            foodid.put(obj)
        }

        paramas.put("user_id", uid.toString())
        paramas.put("restaurant_id", res_id)
        paramas.put("total_cost", total_cost)

        var jsonparamas = JSONObject(paramas as Map<*, *>)
        jsonparamas.put("food", foodid)
        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, jsonparamas, Response.Listener {
                val responseJsonObjectData = it.getJSONObject("data")
                val success = responseJsonObjectData.getBoolean("success")
                if (success) {
                    try {
                        AsyncOrderlistDB3(this).execute().get()
                        startActivity(Intent(this,HomePage::class.java))
                        this@OrderPlacedActivity.finish()
                        finishAffinity()
                    } catch (e: JSONException) {
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        this, "${responseJsonObjectData.getString("errorMessage")}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "fed01b69814abb"
                    return headers
                }
            }
        queue.add(jsonObjectRequest)

    },200)
    }
    }
class AsyncOrderlistDB(val context: Context):
    AsyncTask<Void, Void, List<MenuEntity>>(){
    val orderdb= Room.databaseBuilder(context, MenuDatabase::class.java,"Menu_Db").build()
    override fun doInBackground(vararg params: Void?): List<MenuEntity> {
        return orderdb.menudao().selectallmenu()

    }

}
class AsyncOrderlistDB3(val context:Context):
    AsyncTask<Void, Void, Boolean>(){
    val menudb= Room.databaseBuilder(context, MenuDatabase::class.java,"Menu_Db").build()
    override fun doInBackground(vararg params: Void?): Boolean {
        menudb.menudao().deleteall()
        menudb.close()
        return true
    }}