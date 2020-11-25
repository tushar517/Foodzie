package com.example.foodzie.Fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.example.foodzie.R

class ProfileFragment : Fragment() {
    lateinit var nameet: TextView
    lateinit var phonenum: TextView
    lateinit var emailid:TextView
    lateinit var add: TextView
    lateinit var user_id:TextView
    lateinit var sharedpreferences: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_profile, container, false)
        nameet=view.findViewById(R.id.user_name)
        phonenum=view.findViewById(R.id.user_phone)
        emailid=view.findViewById(R.id.user_email)
        add=view.findViewById(R.id.user_address)
        sharedpreferences=this.requireActivity().getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
        nameet.text=sharedpreferences.getString("name","")
        phonenum.text=sharedpreferences.getString("phone","")
        emailid.text=sharedpreferences.getString("email","")
        add.text=sharedpreferences.getString("address","")

        return view
    }

}