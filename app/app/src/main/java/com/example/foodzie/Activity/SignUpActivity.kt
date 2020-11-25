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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {
    lateinit var nameet: EditText
    lateinit var phonenum: EditText
    lateinit var emailid: EditText
    lateinit var add: EditText
    lateinit var pass: EditText
    lateinit var cnfpass: EditText
    lateinit var signup: Button
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        nameet=findViewById(R.id.Name)
        phonenum=findViewById(R.id.Phone_Number)
        emailid=findViewById(R.id.email)
        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name), MODE_PRIVATE)
        add=findViewById(R.id.address)
        pass=findViewById(R.id.Password)
        cnfpass=findViewById(R.id.ConfirmPassword)
        signup=findViewById(R.id.Signup)
        val queue= Volley.newRequestQueue(this)
        val url="http://13.235.250.119/v2/register/fetch_result"
        var intent= Intent(this@SignUpActivity, HomePage::class.java)
        var paramas= HashMap<String,String>()


        signup.setOnClickListener {
            val user_name=nameet.text.toString()
            val user_phone=phonenum.text.toString()
            val user_pass= pass.text.toString()
            val user_add=add.text.toString()
            val user_email=emailid.text.toString()

            paramas.put("name",user_name)
            paramas.put("mobile_number",user_phone)
            paramas.put("password",user_pass)
            paramas.put("address",user_add)
            paramas.put("email",user_email)
            var jsonparamas=JSONObject(paramas as Map<*, *>)

            if(isvalid()){

            val jsonObjectRequest=
                object :JsonObjectRequest(Method.POST,url,jsonparamas,Response.Listener {
                    val responseJsonObjectData = it.getJSONObject("data")
                    val success = responseJsonObjectData.getBoolean("success")
                    if (success){
                    try{
                        val data=it.getJSONObject("data")
                        val da=data.getJSONObject("data")
                        sharedPreferences.edit().putString("user_id",da.getString("user_id")).apply()
                        sharedPreferences.edit().putString("name",da.getString("name")).apply()
                        sharedPreferences.edit().putString("phone",da.getString("mobile_number")).apply()
                        sharedPreferences.edit().putString("email",da.getString("email")).apply()
                        sharedPreferences.edit().putString("address",da.getString("address")).apply()
                        sharedPreferences.edit().putBoolean("islogin",true).apply()
                        startActivity(intent)
                        this@SignUpActivity.finish()
                    }
                    catch (e:JSONException) {
                        Toast.makeText(this, "error occured ${e}", Toast.LENGTH_SHORT).show()
                    }
                    }
                    else{
                            Toast.makeText(this,"${responseJsonObjectData.getString("errorMessage")}", Toast.LENGTH_SHORT).show()

                        }

                },Response.ErrorListener {
                     Toast.makeText(this,"Volley error occured",Toast.LENGTH_SHORT).show()
                })

                {
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
    fun isvalid():Boolean{
        if(nameet.text.toString().length==0){
            nameet.setError("First Name can't be empty")
            return false
        }
        if(emailid.text.toString().length==0){
            emailid.setError("Email Id can't be empty")
            return false
        }
        if(emailid.text.toString().length<8){
            emailid.setError("Email Id to short")
            return false
        }

        if(!pass.text.toString().equals(cnfpass.text.toString()))
        {
            pass.setError("Password does not match")
            cnfpass.setError("Password does not match")
            return false
        }

        if(phonenum.text.toString().length<10){
            phonenum.setError("Phone Number to short")
            return false
        }
        if(add.text.toString().length<6){
            add.setError("address to short")
            return false
        }

        return true
    }
}