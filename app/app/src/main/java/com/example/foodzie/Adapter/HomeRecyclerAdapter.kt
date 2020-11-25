package com.example.foodzie.Adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodzie.Activity.MenuActivity
import com.example.foodzie.Database.RestrauntDatabase
import com.example.foodzie.Database.RestrauntEntity
import com.example.foodzie.Model.Restraunt
import com.example.foodzie.R
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context,val reslist:ArrayList<Restraunt>):RecyclerView.Adapter<HomeRecyclerAdapter.HomeviewHolder>(){
    class HomeviewHolder(view:View):RecyclerView.ViewHolder(view)
    {
        val nameet:TextView=view.findViewById(R.id.res_name)
        val rating:TextView=view.findViewById(R.id.res_rating)
        val img:ImageView=view.findViewById(R.id.res_img)
        val cost:TextView=view.findViewById(R.id.res_cost)
        val fav:ImageView=view.findViewById(R.id.res_fav)
        val homelayout:LinearLayout=view.findViewById(R.id.homelinearlayout)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeviewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.home_single_row,parent,false)
        return HomeviewHolder(view)

    }

    override fun onBindViewHolder(holder: HomeviewHolder, position: Int) {

        val rest=reslist[position]
        holder.nameet.text=rest.resname
        holder.cost.text=rest.cost+"/person"
        holder.rating.text=rest.rating
        Picasso.get().load(rest.imgurl).error(R.drawable.logo).into(holder.img)
        val restrauntEntity=RestrauntEntity(
            rest.resid.toInt(),
            rest.userid,
            rest.resname,
            rest.rating,
            rest.cost,
            rest.imgurl
        )
        val checkfav=ASyncDB(context,restrauntEntity,1).execute()
        val isfav=checkfav.get()
        if (isfav){
        holder.fav.setImageResource(R.drawable.ic_baseline_favorite_24)
        }
        else{
            holder.fav.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
        holder.fav.setOnClickListener {
            if (!ASyncDB(context,restrauntEntity,1).execute().get()){
              val async=ASyncDB(context,restrauntEntity,2).execute()
                val result=async.get()
                if (result){
                Toast.makeText(context,"Restraunt added to favorites",Toast.LENGTH_SHORT).show()
                holder.fav.setImageResource(R.drawable.ic_baseline_favorite_24)
                }
                else{
                    Toast.makeText(context,"Some Error Occured",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                val async=ASyncDB(context,restrauntEntity,3).execute()
                val result=async.get()
                if (result){
                    Toast.makeText(context,"Restraunt removed from favorites",Toast.LENGTH_SHORT).show()
                    holder.fav.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                }
                else{
                    Toast.makeText(context,"Some Error Occured",Toast.LENGTH_SHORT).show()

                }
            }

        }
        val intent=Intent( context,MenuActivity::class.java)
        holder.homelayout.setOnClickListener {
            intent.putExtra("res_name",rest.resname)
            intent.putExtra("res_id",rest.resid)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return reslist.size
    }
    class ASyncDB(
        val context: Context,
        val restrauntEntity: RestrauntEntity,
        val mode:Int,

    ):AsyncTask<Void,Void,Boolean>(){
        /*
   mode 1->check whether the book is favourite or not
   mode2->add book to favourite
   mode3->delete book from favourite
   */
        val db=Room.databaseBuilder(context,RestrauntDatabase::class.java,"Res-Db${restrauntEntity.user_id}").fallbackToDestructiveMigration().build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                1->{
                    val restraunt:RestrauntEntity?=db.resDAO().selectbyid(restrauntEntity.res_id.toString(),restrauntEntity.user_id)
                    db.close()
                    return restraunt!=null
                }
                2->{
                     db.resDAO().insertres(restrauntEntity)
                    db.close()
                    return true
                }
                3->{
                    db.resDAO().deleteres(restrauntEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}