package com.example.foodzie.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodzie.R
import org.json.JSONException
import org.json.JSONObject

class ForgotPassword : AppCompatActivity() {

    lateinit var etphone: EditText
    lateinit var etEmail: EditText
    lateinit var btnnext:Button
    lateinit var sharedpreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        title="Forget Password"
        etphone=findViewById(R.id.Phone_Number)
        etEmail=findViewById(R.id.Email_ID)
        btnnext=findViewById(R.id.btnnext)
        val queue= Volley.newRequestQueue(this)
        val url="http://13.235.250.119/v2/forgot_password/fetch_result"
        var paramas= HashMap<String,String>()
        sharedpreferences=getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        var intent= Intent(this@ForgotPassword, HomePage::class.java)
        btnnext.setOnClickListener {
            val phone= etphone.text.toString()
            val email= etEmail.text.toString()
            paramas.put("mobile_number",phone)
            paramas.put("email",email)
            var jsonparamas= JSONObject(paramas as Map<*, *>)
            val jsonObjectRequest=
                object :JsonObjectRequest(Method.POST,url,jsonparamas,Response.Listener {
                    val responseJsonObjectData = it.getJSONObject("data")
                    val success = responseJsonObjectData.getBoolean("success")
                    if(success){
                        try {
                         if (responseJsonObjectData.getBoolean("first_try")){
                             Toast.makeText(this,"OTP sent",Toast.LENGTH_SHORT).show()
                             startActivity(Intent(this,ResetPasswordActivity::class.java))
                             sharedpreferences.edit().putBoolean("islogin",false).apply()

                             finish()
                             }
                            else{
                             Toast.makeText(this,"OTP already sent",Toast.LENGTH_SHORT).show()
                             startActivity(Intent(this,ResetPasswordActivity::class.java))
                             sharedpreferences.edit().putBoolean("islogin",false).apply()

                             finish()
                         }
                        }catch (e:JSONException){
                            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this,responseJsonObjectData.getString("errorMessage"),Toast.LENGTH_SHORT).show()
                    }
                },Response.ErrorListener {
                    Toast.makeText(this,"Error occured",Toast.LENGTH_SHORT).show()

                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "fed01b69814abb"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)

            startActivity(intent)
        }
    }
}