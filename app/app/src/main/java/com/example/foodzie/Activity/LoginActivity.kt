package com.example.foodzie.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodzie.R
import com.example.myapplication.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var btnlogin:Button
    lateinit var btnregister:TextView
    lateinit var btnfrgt:Button
    lateinit var etphone:EditText
    lateinit var etpass:EditText
   lateinit var sharedpreferences:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title="LOG IN"
        btnlogin=findViewById(R.id.Login)
        btnregister=findViewById(R.id.register)
        btnfrgt=findViewById(R.id.frgtpass)
        etphone=findViewById(R.id.Phone)
        etpass=findViewById(R.id.Password)
        var intent=Intent(this@LoginActivity, HomePage::class.java)
        if (!ConnectionManager().checkconnectivity(this)){
            val dialog= AlertDialog.Builder(this)
            dialog.setTitle("Error").setMessage("Internet Connection not found")
            dialog.setPositiveButton("Open Setting",{text,listener->
                val opensettings= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(opensettings)
                finish()
            })
            dialog.setNegativeButton("Exit App",{text,listener->
                ActivityCompat.finishAffinity(this)
            })
            dialog.create().show()
        }
        else{
        sharedpreferences=getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val islogin=sharedpreferences.getBoolean("islogin",false)
        if (islogin){
            startActivity(intent)
            this@LoginActivity.finish()
        }
        btnregister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }

        btnfrgt.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPassword::class.java))
            sharedpreferences.edit().putBoolean("islogin",false).apply()
        }
            var paramas= HashMap<String,String>()
            val queue= Volley.newRequestQueue(this)
            val url="http://13.235.250.119/v2/login/fetch_result"

            btnlogin.setOnClickListener {
            val phone=etphone.text.toString()
            val pass=etpass.text.toString()
            paramas.put("mobile_number",phone)
            paramas.put("password",pass)
            var jsonparamas= JSONObject(paramas as Map<*, *>)
            val jsonObjectRequest=
                object :JsonObjectRequest(Method.POST,url,jsonparamas,Response.Listener {
                    val responseJsonObjectData = it.getJSONObject("data")
                    val success = responseJsonObjectData.getBoolean("success")
                    if (success){
                    try {
                        val data=it.getJSONObject("data")
                        val da=data.getJSONObject("data")
                        sharedpreferences.edit().putBoolean("islogin",true).apply()
                        sharedpreferences.edit().putString("name",da.getString("name")).apply()
                        sharedpreferences.edit().putString("user_id",da.getString("user_id")).apply()
                        sharedpreferences.edit().putString("phone",da.getString("mobile_number")).apply()
                        sharedpreferences.edit().putString("email",da.getString("email")).apply()
                        sharedpreferences.edit().putString("address",da.getString("address")).apply()
                        startActivity(intent)
                        this@LoginActivity.finish()
                    }catch (e:JSONException){
                        Toast.makeText(this,"error occured ${e}",Toast.LENGTH_SHORT).show()

                    }
                    }
                    else{
                        Toast.makeText(this,"${responseJsonObjectData.getString("errorMessage")}",Toast.LENGTH_SHORT).show()
                    }
                },Response.ErrorListener {
                    Toast.makeText(this,"error occures",Toast.LENGTH_SHORT).show()
                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "fed01b69814abb"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)

    }

        }
}

    }