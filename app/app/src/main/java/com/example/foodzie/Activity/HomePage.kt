package com.example.foodzie.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.example.foodzie.Fragment.*
import com.example.foodzie.R
import kotlinx.android.synthetic.main.nav_header_main.view.*

class HomePage : AppCompatActivity() {
     var backpresstime:Long=0
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var frameLayout: FrameLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var sharedpreferences: SharedPreferences
    var previousmenuitem: MenuItem?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout=findViewById(R.id.drawer_layout)
        coordinatorLayout=findViewById(R.id.coordinator_layout)
        frameLayout=findViewById(R.id.mainframe)
        sharedpreferences=getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        navigationView=findViewById(R.id.nav_view)
        setuptoolbar()
        val headerview=navigationView.getHeaderView(0)
        val head_name=headerview.head_name
        val head_email=headerview.head_email
        head_name.text=sharedpreferences.getString("name","")
        head_email.text=sharedpreferences.getString("email","")
        val actionBarDrawerToggle= ActionBarDrawerToggle(this,drawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        openhome()
        navigationView.setNavigationItemSelectedListener {
            if(previousmenuitem!=null){
                previousmenuitem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousmenuitem=it
            when(it.itemId){
                R.id.nav_home->{
                    openhome()
                    drawerLayout.closeDrawers()
                }
                R.id.nav_favorite->{
                    supportFragmentManager.beginTransaction().replace(R.id.mainframe,
                            FavoriteFragment()
                    ).commit()
                    supportActionBar?.title="Favorites"
                    drawerLayout.closeDrawers()
                }
                R.id.nav_profile->{
                    supportFragmentManager.beginTransaction().replace(R.id.mainframe,
                            ProfileFragment()).commit()
                    supportActionBar?.title="Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.FAQs->{
                    supportFragmentManager.beginTransaction().replace(R.id.mainframe,
                            FaqsFragment()
                    ).commit()
                    supportActionBar?.title="Frequently Asked Questions"
                    drawerLayout.closeDrawers()
                }
                R.id.logout-> {
                    val dialog= AlertDialog.Builder(this@HomePage)
                    dialog.setTitle("Confirmation").setMessage("Are you sure you want to log out?")
                    dialog.setPositiveButton("Log Out",{text,listener->
                        sharedpreferences.edit().putBoolean("islogin",false).apply()
                        startActivity(Intent(this@HomePage, LoginActivity::class.java))
                        this@HomePage.finish()
                        drawerLayout.closeDrawers()


                    })
                    dialog.setNegativeButton("No",{text,listener->
                        when(supportFragmentManager.findFragmentById(R.id.mainframe)){
                            is HomeFragment->navigationView.setCheckedItem(R.id.nav_home)
                            is FaqsFragment->navigationView.setCheckedItem(R.id.FAQs)
                            is ProfileFragment->navigationView.setCheckedItem(R.id.nav_profile)
                            is FavoriteFragment->navigationView.setCheckedItem(R.id.nav_favorite)
                        }
                        drawerLayout.closeDrawers()

                    })
                    dialog.create().show()

                }
                R.id.nav_orderhistory->{
                    supportFragmentManager.beginTransaction().replace(R.id.mainframe,
                        OrderHistoryFragment()
                    ).commit()
                    supportActionBar?.title="Order History"
                    drawerLayout.closeDrawers()
                }

            }
            return@setNavigationItemSelectedListener true
        }
    }
    fun setuptoolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Foodzie"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
    fun openhome(){
        supportFragmentManager.beginTransaction().replace(R.id.mainframe, HomeFragment()).commit()
        navigationView.setCheckedItem(R.id.nav_home)
        supportActionBar?.title="Restraunts"
    }
    override fun onBackPressed() {
        val frag=supportFragmentManager.findFragmentById(R.id.mainframe)
        when(frag){
            !is HomeFragment->openhome()
            else ->{
                if (backpresstime+2000>System.currentTimeMillis()){
                    super.onBackPressed()
                    return
                }
                else{
                    Toast.makeText(this,"Press back again to exit",Toast.LENGTH_SHORT).show()
                }
                backpresstime=System.currentTimeMillis()
            }
        }
    }


    }
