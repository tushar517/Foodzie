package com.example.foodzie.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodzie.Database.MenuEntity
import com.example.foodzie.R

class CartRecyclerAdapter(val context: Context,val orderlist:ArrayList<MenuEntity>):RecyclerView.Adapter<CartRecyclerAdapter.CartviewHolder>() {
    class CartviewHolder(view: View):RecyclerView.ViewHolder(view){
        val name:TextView=view.findViewById(R.id.dish_name)
        val cost:TextView=view.findViewById(R.id.dish_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartviewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.single_row_cart,parent,false)
        return CartviewHolder(view)
    }

    override fun onBindViewHolder(holder: CartviewHolder, position: Int) {
        val or=orderlist[position]
        holder.name.text=or.name
        holder.cost.text=or.cost

    }

    override fun getItemCount(): Int {
        return orderlist.size   }
}