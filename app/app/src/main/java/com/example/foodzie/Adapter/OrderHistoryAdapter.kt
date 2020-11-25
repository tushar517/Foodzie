package com.example.foodzie.Adapter

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings.Secure.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodzie.Model.order
import com.example.foodzie.R
import com.squareup.picasso.Picasso

class OrderHistoryAdapter(val context: Context,val orderlist:ArrayList<order>):RecyclerView.Adapter<OrderHistoryAdapter.orderViewHolder>() {
    class orderViewHolder(val view:View):RecyclerView.ViewHolder(view){
        val resname:TextView=view.findViewById(R.id.resname)
        val it_order_recycler:RecyclerView=view.findViewById(R.id.itemrecycler)
        val it_ordered_on:TextView=view.findViewById(R.id.ordered_on)
        val total_amt:TextView=view.findViewById(R.id.total_amt)
        lateinit var layoutManager:RecyclerView.LayoutManager
        lateinit var itemAdapter:ItemRecyclerAdapter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): orderViewHolder {
        val view=
            LayoutInflater.from(parent.context).inflate(R.layout.single_row_orderhistory,parent,false)
        return orderViewHolder(view)
    }

    override fun onBindViewHolder(holder: orderViewHolder, position: Int) {
        val order=orderlist[position]
        holder.resname.text=order.res_name
        holder.itemAdapter=ItemRecyclerAdapter(context,order.food)
        holder.layoutManager=LinearLayoutManager(context)
        holder.it_order_recycler.adapter=holder.itemAdapter
        holder.it_order_recycler.layoutManager=holder.layoutManager
        holder.it_ordered_on.text=order.order_on
        holder.total_amt.text=order.amount
    }

    override fun getItemCount(): Int {
        return orderlist.size
    }


}