package com.example.foodzie.Adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodzie.Activity.CartActivity
import com.example.foodzie.Database.MenuDatabase
import com.example.foodzie.Database.MenuEntity
import com.example.foodzie.Database.RestrauntDatabase
import com.example.foodzie.Database.RestrauntEntity
import com.example.foodzie.Model.Menu
import com.example.foodzie.Model.Restraunt
import com.example.foodzie.R

class MenuRecyclerAdapter (val context: Context,val menulist:ArrayList<Menu>):RecyclerView.Adapter<MenuRecyclerAdapter.MenuviewHolder>() {
    class MenuviewHolder(view: View) : RecyclerView.ViewHolder(view){
        val menu_name:TextView=view.findViewById(R.id.menu_name)
        val menu_cost:TextView=view.findViewById(R.id.menu_cost)
        val sno:TextView=view.findViewById(R.id.sno)
        val addtocart:ImageView=view.findViewById(R.id.addtocart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuviewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.single_row_menu,parent,false)
        return MenuviewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuviewHolder, position: Int) {
        val menu=menulist[position]
        holder.menu_name.text=menu.name
        holder.menu_cost.text=menu.cost
        holder.sno.text=(position+1).toString()
        val menuentity=MenuEntity(
            menu.id,
            menu.name,
            menu.cost,
            menu.res_id
        )
        val check=AsyncMenuDB(context,menuentity,1).execute().get()
        if (check){
            holder.addtocart.setImageResource(R.drawable.ic_baseline_remove_24)
        }
        else{
            holder.addtocart.setImageResource(R.drawable.ic_baseline_add_24)
        }

        holder.addtocart.setOnClickListener {
            if(!AsyncMenuDB(context,menuentity,1).execute().get()){
                val async=AsyncMenuDB(context,menuentity,2).execute()
                val result=async.get()
                if (result){
                    Toast.makeText(context,"order added",Toast.LENGTH_SHORT).show()
                    holder.addtocart.setImageResource(R.drawable.ic_baseline_remove_24)

                }
            }
            else{
                val async=AsyncMenuDB(context,menuentity,3).execute()
                val result=async.get()
                if (result){
                    Toast.makeText(context,"order Removed",Toast.LENGTH_SHORT).show()
                    holder.addtocart.setImageResource(R.drawable.ic_baseline_add_24)
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return menulist.size
    }

}
class AsyncMenuDB(val context:Context,val Menu:MenuEntity,val mode:Int):AsyncTask<Void,Void,Boolean>(){
    val menudb=Room.databaseBuilder(context,MenuDatabase::class.java,"Menu_Db").build()
    override fun doInBackground(vararg params: Void?): Boolean {
        when(mode){
            1->{
                val menus:MenuEntity?=menudb.menudao().selectbyid(Menu.menu_id)
                menudb.close()
                return menus!=null
            }
            2->{
                menudb.menudao().insertmenu(Menu)
                menudb.close()
                return true
            }
            3->{
                menudb.menudao().deletemenu(Menu)
                menudb.close()
                return true
            }
            4->{
                menudb.menudao().count()
                menudb.close()
                return true
            }
        }
        return false
    }
}