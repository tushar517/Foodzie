package com.example.foodzie.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ActionMode
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodzie.Adapter.MenuRecyclerAdapter
import com.example.foodzie.Database.MenuDatabase
import com.example.foodzie.Database.MenuEntity
import com.example.foodzie.Model.Menu
import com.example.foodzie.R
import kotlinx.android.synthetic.main.homepage.*
import org.json.JSONException

class MenuActivity : AppCompatActivity() {
    lateinit var prglay:RelativeLayout
    lateinit var prgtxt:TextView
    lateinit var prgbar:ProgressBar
    lateinit var menurec:RecyclerView
     var menulist= arrayListOf<Menu>()
    lateinit var menuRecyclerAdapter: MenuRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var gocart:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        title="Menu"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        prgbar=findViewById(R.id.prgbar)
        gocart=findViewById(R.id.gotocartbtn)
        prgtxt=findViewById(R.id.prgtxt)
        prglay=findViewById(R.id.progresslayout)
        menurec=findViewById(R.id.menurecycler)
        layoutManager=LinearLayoutManager(this)
        val queue=Volley.newRequestQueue(this)
        val resid=intent.getStringExtra("res_id")
        val url="http://13.235.250.119/v2/restaurants/fetch_result/$resid"
        val jsonObjectRequest=
            object :JsonObjectRequest(Method.GET,url,null,Response.Listener {
                try{
                    prglay.visibility=View.GONE
                    prgbar.visibility=View.GONE
                    prgtxt.visibility=View.GONE
                    val responseJsonObjectData = it.getJSONObject("data")
                    val success = responseJsonObjectData.getBoolean("success")
                    if (success){
                        val data=it.getJSONObject("data")
                        val da=data.getJSONArray("data")
                        for (i in 0 until da.length()){
                            val resjson=da.getJSONObject(i)
                            val menu=Menu(
                                resjson.getString("id"),
                                resjson.getString("name"),
                                resjson.getString("cost_for_one"),
                                resjson.getString("restaurant_id")
                            )
                            menulist.add(menu)
                            menuRecyclerAdapter= MenuRecyclerAdapter(this,menulist)
                            menurec.adapter=menuRecyclerAdapter
                            menurec.layoutManager=layoutManager
                            menurec.addItemDecoration(
                                DividerItemDecoration(
                                    menurec.context,
                                    (layoutManager as LinearLayoutManager).orientation
                                )
                            )

                        }
                    }
                    else{
                        Toast.makeText(this,"unable to fetch information",Toast.LENGTH_LONG).show()

                    }
                }catch (e:JSONException){
                    Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
                }
            },Response.ErrorListener {
                Toast.makeText(this,"Error occured ",Toast.LENGTH_SHORT).show()

            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "fed01b69814abb"
                    return headers
            }
    }
        queue.add(jsonObjectRequest)
        val name=intent.getStringExtra("res_name")
        gocart.setOnClickListener {
             val intent1=Intent(this,CartActivity::class.java)
            intent1.putExtra("res_name",name)
            startActivity(intent1)

        }

    }
fun iscartempty(): Boolean {
    val check=AsyncMenuDB(this,1).execute().get()
    return check
}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (iscartempty()){
            val dialog=AlertDialog.Builder(this).setTitle("Warning").
            setMessage("Are you sure you want to clear your cart and go back?").setPositiveButton("Yes",{text,listener->
                AsyncMenuDB(this,2).execute()
                startActivity(Intent(this,HomePage::class.java))
                finish()
            })
                .setNegativeButton("No",{text,listener->

                })
            dialog.create().show()
        }
else {
            startActivity(Intent(this,HomePage::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onBackPressed() {
        if (iscartempty()){
            val dialog=AlertDialog.Builder(this).setTitle("Warning").
            setMessage("Are you sure you want to clear your cart and go back?").setPositiveButton("Yes",{text,listener->
                AsyncMenuDB(this,2).execute()
                startActivity(Intent(this,HomePage::class.java))
            })
                .setNegativeButton("No",{text,listener->})
            dialog.create().show()
        }
        else{
        super.onBackPressed()
        }
    }

    override fun onDestroy() {
        AsyncMenuDB(this,2).execute()
        super.onDestroy()
    }
}
class AsyncMenuDB(val context:Context,  val mode:Int):
    AsyncTask<Void, Void, Boolean>(){
    val menudb= Room.databaseBuilder(context, MenuDatabase::class.java,"Menu_Db").build()
    override fun doInBackground(vararg params: Void?): Boolean {
        when(mode){
            1->{
                val count=menudb.menudao().count()
                menudb.close()
                if (count>0){
                return true}
                else{
                    return false
                }
            }
            2->{
                menudb.menudao().deleteall()
                menudb.close()
                return true
            }
        }
        return false
    }
}