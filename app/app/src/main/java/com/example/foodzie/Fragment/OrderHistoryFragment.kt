package com.example.foodzie.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodzie.Adapter.OrderHistoryAdapter
import com.example.foodzie.Model.order
import com.example.foodzie.R
import org.json.JSONException

class OrderHistoryFragment : Fragment() {
    lateinit var orderhisrecycler:RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var orderHistoryAdapter:OrderHistoryAdapter
    var orderlist= arrayListOf<order>()
    lateinit var sharedpreferences: SharedPreferences

    lateinit var prglay: RelativeLayout
    lateinit var prgtxt: TextView
    lateinit var prgbar: ProgressBar
    lateinit var headingtextview:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sharedpreferences=this.requireActivity().getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
        val view= inflater.inflate(R.layout.fragment_order_history, container, false)
        headingtextview=view.findViewById(R.id.heading)
        prgbar=view.findViewById(R.id.prgbar)
        prgtxt=view.findViewById(R.id.prgtxt)
        prglay=view.findViewById(R.id.progresslayout)
        headingtextview.visibility=View.GONE
        orderhisrecycler=view.findViewById(R.id.orderhisreycler)
        layoutManager=LinearLayoutManager(activity as Context)
        val uid=sharedpreferences.getString("user_id","")
        val queue= Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v2/orders/fetch_result/$uid"
        val jsonObjectRequest=
            object :JsonObjectRequest(Method.GET,url,null,Response.Listener {
                try {
                    val responseJsonObjectData = it.getJSONObject("data")
                    val success = responseJsonObjectData.getBoolean("success")
                    if (success){
                        headingtextview.visibility=View.VISIBLE
                        prglay.visibility=View.GONE
                        prgbar.visibility=View.GONE
                        prgtxt.visibility=View.GONE
                        val data=responseJsonObjectData.getJSONArray("data")
                        for (i in 0 until data.length()){
                            val orderjson=data.getJSONObject(i)
                            val orders=order(
                                orderjson.getString("order_id"),
                                orderjson.getString("restaurant_name"),
                                orderjson.getString("total_cost"),
                                orderjson.getString("order_placed_at"),
                                orderjson.getJSONArray("food_items")
                            )
                            orderlist.add(orders)
                            orderHistoryAdapter= OrderHistoryAdapter(activity as Context,orderlist)
                            orderhisrecycler.adapter=orderHistoryAdapter
                            orderhisrecycler.layoutManager=layoutManager
                            orderhisrecycler.addItemDecoration(
                                DividerItemDecoration(
                                    orderhisrecycler.context,
                                    (layoutManager as LinearLayoutManager).orientation
                                )
                            )
                        }

                    }
                    else{
                        Toast.makeText(activity as Context,success.toString(),Toast.LENGTH_SHORT).show()

                    }

                }catch (e:JSONException){
                    Toast.makeText(activity as Context,e.toString(),Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {

            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "fed01b69814abb"
                    return headers
            }
            }
        queue.add(jsonObjectRequest)
        return view
    }

}