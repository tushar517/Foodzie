package com.example.foodzie.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Global.getString
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodzie.Adapter.FavoriteRecyclerAdapter
import com.example.foodzie.Database.RestrauntDatabase
import com.example.foodzie.Database.RestrauntEntity
import com.example.foodzie.R

class FavoriteFragment : Fragment() {
    lateinit var favrecycler:RecyclerView
    lateinit var prgbar: ProgressBar
    lateinit var prglay: RelativeLayout
    lateinit var prgtxt:TextView
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var sharedpreferences: SharedPreferences
    lateinit var no_favtxt:TextView
    lateinit var recycleradapter: FavoriteRecyclerAdapter
    var restrauntlist= mutableListOf<RestrauntEntity>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view= inflater.inflate(R.layout.fragment_favorite, container, false)
        favrecycler=view.findViewById(R.id.favoriteRecycler)
        prgbar=view.findViewById(R.id.prgbar)
        prglay=view.findViewById(R.id.progresslayout)
        prgtxt=view.findViewById(R.id.prgtxt)
        prglay.visibility=View.VISIBLE
        prgbar.visibility=View.VISIBLE
        prgtxt.visibility=View.VISIBLE
        no_favtxt=view.findViewById(R.id.no_fav)
        sharedpreferences=this.requireActivity().getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
        no_favtxt.visibility=View.GONE
        var user_id=sharedpreferences.getString("user_id","")

        restrauntlist= RetrieveFav(activity as Context,user_id).execute().get() as MutableList<RestrauntEntity>
        if (countFav(activity as Context,user_id).execute().get()>0) {
            layoutManager = GridLayoutManager(activity as Context, 2)
            if (activity != null) {
                prglay.visibility = View.GONE
                prgbar.visibility = View.GONE
                prgtxt.visibility = View.GONE
                recycleradapter = FavoriteRecyclerAdapter(activity as Context, restrauntlist)
                favrecycler.adapter = recycleradapter
                favrecycler.layoutManager = layoutManager
            }
        }
        else{
            prglay.visibility = View.GONE
            prgbar.visibility = View.GONE
            prgtxt.visibility = View.GONE
            no_favtxt.visibility=View.VISIBLE
        }
        return view
    }

    class  RetrieveFav(val context: Context,val uid:String?): AsyncTask<Void, Void, List<RestrauntEntity>>(){
        override fun doInBackground(vararg params: Void?): List<RestrauntEntity> {

            val db= Room.databaseBuilder(context,RestrauntDatabase::class.java,"Res-Db${uid}").fallbackToDestructiveMigration().build()
            return db.resDAO().selectallres()

        }

    }
    class  countFav(val context: Context,val uid:String?): AsyncTask<Void, Void, Int>(){
        override fun doInBackground(vararg params: Void?): Int {

            val db= Room.databaseBuilder(context,RestrauntDatabase::class.java,"Res-Db${uid}").fallbackToDestructiveMigration().build()
            return db.resDAO().count()

        }

    }

}