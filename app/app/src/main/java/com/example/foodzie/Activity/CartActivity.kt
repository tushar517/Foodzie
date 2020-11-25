package com.example.foodzie.Activity

import android.app.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.FtsOptions
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.example.foodzie.Adapter.CartRecyclerAdapter
import com.example.foodzie.Database.MenuDatabase
import com.example.foodzie.R
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultListener
import com.razorpay.PaymentResultWithDataListener
import kotlinx.android.synthetic.main.single_row_orderhistory.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Error
import com.example.foodzie.Database.MenuEntity as MenuEntity

class CartActivity : AppCompatActivity(),PaymentResultWithDataListener {
    var order= arrayListOf<MenuEntity>()
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var orderadapter:CartRecyclerAdapter
    lateinit var orderrec:RecyclerView
    lateinit var res_name:TextView
    lateinit var total:TextView
    lateinit var noorder:TextView
    lateinit var titles:TextView
    lateinit var orderbtn:Button
    lateinit var totalhead:TextView
    lateinit var itemcount:TextView
    lateinit var totalitem:TextView
    lateinit var sharedPreferences: SharedPreferences
    val foodid = JSONArray()
    var orderid=""

    var bill=0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        title="Cart"
        res_name=findViewById(R.id.titlename)
        res_name.text=intent.getStringExtra("res_name")
        orderrec=findViewById(R.id.cartrecycler)
        total=findViewById(R.id.price)
        totalhead=findViewById(R.id.totalcost)
        titles=findViewById(R.id.titlehead)
        noorder=findViewById(R.id.no_order)
        orderbtn=findViewById(R.id.placeorder)
        itemcount=findViewById(R.id.count)
        totalitem=findViewById(R.id.totalitem)
        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name), MODE_PRIVATE)
        order=AsyncOrderDB(this).execute().get() as ArrayList<MenuEntity>
        val item=AsyncOrderDB2(this).execute().get()
        if(this!=null){

        if (item>0){
            totalhead.visibility=View.VISIBLE
            itemcount.visibility=View.VISIBLE
            totalitem.visibility=View.VISIBLE
            titles.visibility=View.VISIBLE
            res_name.visibility=View.VISIBLE
            total.visibility=View.VISIBLE
            noorder.visibility=View.GONE
            orderbtn.visibility= View.VISIBLE

            totalitem.text=item.toString()
            layoutManager=LinearLayoutManager(this)
            orderadapter= CartRecyclerAdapter(this,order)
            orderrec.adapter=orderadapter
            orderrec.layoutManager=layoutManager

            for(i in order){
                bill+=i.cost.toDouble()
                val obj = JSONObject()
                obj.put("food_item_id",i.menu_id)
                foodid.put(obj)
                            }
        }
            else{
            totalhead.visibility=View.GONE
            itemcount.visibility=View.GONE
            totalitem.visibility=View.GONE
            titles.visibility=View.GONE
            res_name.visibility=View.GONE
            total.visibility=View.GONE
            noorder.visibility=View.VISIBLE
            orderbtn.visibility=View.GONE
        }
        }
        total.text=bill.toString()


        orderbtn.setOnClickListener {
            startPayment()
        }
    }
    private fun startPayment() {
        val emailid=sharedPreferences.getString("email","")
        val number=sharedPreferences.getString("phone","")
        val name=sharedPreferences.getString("name","")
        val activity: Activity = this
        val co = Checkout()

        co.setKeyID("rzp_test_CbdLT2TyKs9gj2")
        try {
            val options = JSONObject()
            options.put("name",name)
            options.put("currency","INR");
            val total=bill*100
            options.put("amount","${total}")//pass amount in currency subunits
            val prefill = JSONObject()
            prefill.put("email",emailid)
            prefill.put("contact",number)

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        try {
            val intent=Intent(this,OrderPlacedActivity::class.java)
        intent.putExtra("payment_id",orderid)
        intent.putExtra("restraunt_id",order[0].res_id)
        intent.putExtra("bill",bill.toString())
        startActivity(intent)
        this@CartActivity.finish()
        }
        catch (e:Exception){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
        }

    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(this,p1,Toast.LENGTH_LONG).show()
    }


}
class AsyncOrderDB(val context: Context):
    AsyncTask<Void, Void, List<MenuEntity>>(){
    val orderdb= Room.databaseBuilder(context, MenuDatabase::class.java,"Menu_Db").build()
    override fun doInBackground(vararg params: Void?): List<MenuEntity> {
        return orderdb.menudao().selectallmenu()

    }
}
class AsyncOrderDB2(val context: Context):
    AsyncTask<Void, Void, Int>(){
    val orderdb= Room.databaseBuilder(context, MenuDatabase::class.java,"Menu_Db").build()
    override fun doInBackground(vararg params: Void?): Int {
        return orderdb.menudao().count()

    }
}
