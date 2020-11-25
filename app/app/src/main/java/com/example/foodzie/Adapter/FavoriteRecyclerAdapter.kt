package com.example.foodzie.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.Update
import androidx.room.migration.Migration
import com.example.foodzie.Activity.HomePage
import com.example.foodzie.Activity.LoginActivity
import com.example.foodzie.Activity.MenuActivity
import com.example.foodzie.Database.RestrauntDatabase
import com.example.foodzie.Database.RestrauntEntity
import com.example.foodzie.Fragment.FaqsFragment
import com.example.foodzie.Fragment.FavoriteFragment
import com.example.foodzie.Fragment.HomeFragment
import com.example.foodzie.Fragment.ProfileFragment
import com.example.foodzie.Model.Restraunt
import com.example.foodzie.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.favourite_single_row.view.*

class FavoriteRecyclerAdapter(val context: Context,val restrauntList: MutableList<RestrauntEntity>):RecyclerView.Adapter<FavoriteRecyclerAdapter.favVIewHolder>() {
    class favVIewHolder(view: View):RecyclerView.ViewHolder(view){
        val name:TextView=view.findViewById(R.id.fav_name)
        val cost:TextView=view.findViewById(R.id.fav_cost)
        val rating:TextView=view.findViewById(R.id.fav_rating)
        val img:ImageView=view.findViewById(R.id.fav_img)
        val fav:ImageView=view.findViewById(R.id.res_fav)
        val favlayout:LinearLayout=view.findViewById(R.id.favlinearlayout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): favVIewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.favourite_single_row,parent,false)
        return favVIewHolder(view)
    }

    override fun onBindViewHolder(holder: favVIewHolder, position: Int) {

        val res=restrauntList[position]
        holder.name.text=res.res_name
        holder.cost.text=res.res_rating+"/person"
        holder.rating.text=res.res_rating
        Picasso.get().load(res.res_img).error(R.drawable.logo).into(holder.img)
        holder.fav.setOnClickListener {
            val dialog= AlertDialog.Builder(context)
            dialog.setTitle("Confirmation").setMessage("Are you sure you want to remove ${res.res_name} from favorite?")
            dialog.setPositiveButton("confirm",{text,listener->
                val async= FavoriteRecyclerAdapter.ASyncDB(
                    context,
                    restrauntList[position],
                    res.user_id
                ).execute()
                val result=async.get()
                if (result){
                    Toast.makeText(context,"${res.res_name} removed from favorites", Toast.LENGTH_SHORT).show()

                }
                else{
                    Toast.makeText(context,"Some Error Occured", Toast.LENGTH_SHORT).show()
                }
                restrauntList.removeAt(position)
                notifyItemRemoved(position)
                notifyDataSetChanged()
            })
            dialog.setNegativeButton("No",{text,listener->
            })
            dialog.create().show()

        }
        val intent=Intent(context,MenuActivity::class.java)
        holder.favlayout.setOnClickListener {
            intent.putExtra("res_id",res.res_id.toString())
            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return restrauntList.size
    }
    class ASyncDB(
        val context: Context,
        val restrauntEntity: RestrauntEntity,
         uid:String?

    ):
        AsyncTask<Void, Void, Boolean>(){
        val db= Room.databaseBuilder(context, RestrauntDatabase::class.java,"Res-Db${uid}").fallbackToDestructiveMigration().build()
        override fun doInBackground(vararg params: Void?): Boolean {
                    db.resDAO().deleteres(restrauntEntity)

                    db.close()

                    return true
        }
    }
}