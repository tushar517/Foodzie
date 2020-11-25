package com.example.foodzie.Activity

import android.content.Intent
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

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var phone:EditText
    lateinit var pass:EditText
    lateinit var cnfpass:EditText
    lateinit var OTP:EditText
    lateinit var submit:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        phone=findViewById(R.id.mobileet)
        pass=findViewById(R.id.password)
        cnfpass=findViewById(R.id.cnf_password)
        OTP=findViewById(R.id.OTP)
        submit=findViewById(R.id.submit)
        val queue= Volley.newRequestQueue(this)
        val url="http://13.235.250.119/v2/reset_password/fetch_result"
        var paramas= HashMap<String,String>()

        submit.setOnClickListener {
            val txtphone=phone.text.toString()
            val txtpass=pass.text.toString()
            val txtcnfpass=cnfpass.text.toString()
            val txtOTP=OTP.text.toString()

            if (txtpass.equals(txtcnfpass)){
                paramas.put("mobile_number",txtphone)
                paramas.put("password",txtpass)
                paramas.put("otp",txtOTP)
                var jsonparamas= JSONObject(paramas as Map<*, *>)
                val jsonObjectRequest=
                    object :JsonObjectRequest(Method.POST,url,jsonparamas,Response.Listener {
                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")
                        if(success){
                            try {
                                    startActivity(Intent(this,LoginActivity::class.java))
                                    Toast.makeText(this,responseJsonObjectData.getString("successMessage"),Toast.LENGTH_SHORT).show()
                                    finish()

                            }catch (e: JSONException){
                                Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this,responseJsonObjectData.getString("errorMessage"),Toast.LENGTH_SHORT).show()
                        }
                    },Response.ErrorListener {  }){
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "fed01b69814abb"
                            return headers
                        }
                    }
                queue.add(jsonObjectRequest)


            }else{
                Toast.makeText(this,"passwords does not match",Toast.LENGTH_SHORT).show()
                pass.setError("passwords does not match")
                cnfpass.setError("Passwords does not match")
            }
        }

    }
}