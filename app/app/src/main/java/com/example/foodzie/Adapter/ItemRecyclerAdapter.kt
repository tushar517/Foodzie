package com.example.foodzie.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodzie.R
import org.json.JSONArray

class ItemRecyclerAdapter(val context: Context,val item_list:JSONArray):RecyclerView.Adapter<ItemRecyclerAdapter.ItemViewHolder>(){
    class ItemViewHolder(val view:View):RecyclerView.ViewHolder(view){
        val item_name:TextView=view.findViewById(R.id.dish_name)
        val item_price:TextView=view.findViewById(R.id.dish_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view=
            LayoutInflater.from(parent.context).inflate(R.layout.single_row_item,parent,false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item=item_list.getJSONObject(position)
        holder.item_name.text=item.getString("name")
        holder.item_price.text=item.getString("cost")
    }

    override fun getItemCount(): Int {
        return item_list.length()   }
}