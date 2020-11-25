package com.example.foodzie.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.Volley
import com.example.foodzie.Adapter.HomeRecyclerAdapter
import com.example.foodzie.Model.Restraunt
import com.example.foodzie.R
import com.example.myapplication.util.ConnectionManager
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Response
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class HomeFragment : Fragment() {

    lateinit var progressBar: ProgressBar
    lateinit var proglay:RelativeLayout
    lateinit var progtext:TextView
    lateinit var homerecycler:RecyclerView
    lateinit var no_res:TextView
    lateinit var sharedpreferences: SharedPreferences

    var itemselected=-1
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var homeRecyclerAdapter: HomeRecyclerAdapter
    var reslist=ArrayList<Restraunt>()

    var resinfolist= arrayListOf<Restraunt>()
    var ratingcomparator=Comparator<Restraunt>{ res1, res2->

        if(res1.rating.compareTo(res2.rating,true)==0){
            res1.resname.compareTo(res2.resname,true)
        }else
        {
            res1.rating.compareTo(res2.rating,true)
        }
    }
    var alphabetcomparator=Comparator<Restraunt>{ res1, res2->

        res1.resname.compareTo(res2.resname,true)

    }
    var costcomparator=Comparator<Restraunt>{ res1, res2->
    res1.cost.compareTo(res2.cost,true)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

       val  view = inflater.inflate(R.layout.fragment_home, container, false)
        homerecycler=view.findViewById(R.id.homeRecycler)
        layoutManager= LinearLayoutManager(activity)
        progressBar=view.findViewById(R.id.prgbar)
        sharedpreferences=this.requireActivity().getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)

        proglay=view.findViewById(R.id.progresslayout)
        proglay.visibility=View.VISIBLE
        no_res=view.findViewById(R.id.no_restraunt)
        no_res.visibility=View.GONE
        setHasOptionsMenu(true)

        progtext=view.findViewById(R.id.prgtxt)
        val queue= Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v2/restaurants/fetch_result/"
        if (!ConnectionManager().checkconnectivity(activity as Context)){
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error").setMessage("Internet Connection not found")
            dialog.setPositiveButton("Open Setting",{text,listener->
                val opensettings= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(opensettings)
                activity?.finish()
            })
            dialog.setNegativeButton("Exit App",{text,listener->
                ActivityCompat.finishAffinity(activity as Activity)
            })
            dialog.create().show()
        }
        else{
            val jsonObjectRequest=
                    object : JsonObjectRequest(Method.GET,url,null,Response.Listener{

                        try {
                            proglay.visibility=View.GONE
                            progressBar.visibility=View.GONE
                            progtext.visibility=View.GONE
                            val responseJsonObjectData = it.getJSONObject("data")
                            val success = responseJsonObjectData.getBoolean("success")
                            if (activity!=null){
                            if(success){

                                val data=it.getJSONObject("data")
                                val da=data.getJSONArray("data")
                                for (i in 0 until da.length()){
                                    val resjson=da.getJSONObject(i)
                                    val restraunt=Restraunt(
                                            sharedpreferences.getString("user_id",""),
                                            resjson.getString("id"),
                                            resjson.getString("name"),
                                            resjson.getString("rating"),
                                            resjson.getString("cost_for_one"),
                                            resjson.getString("image_url")
                                    )
                                    resinfolist.add(restraunt)

                                    homeRecyclerAdapter= HomeRecyclerAdapter(activity as Context,resinfolist)
                                    homerecycler.adapter=homeRecyclerAdapter
                                    homerecycler.layoutManager=layoutManager

                                    homerecycler.addItemDecoration(
                                            DividerItemDecoration(
                                                    homerecycler.context,
                                                    (layoutManager as LinearLayoutManager).orientation
                                            )
                                   )
                                }
                                reslist.addAll(resinfolist)

                            }
                            else{
                                Toast.makeText(activity as Context,"unable to fetch information",Toast.LENGTH_LONG).show()

                            }}
                        }
                        catch (e: JSONException ){
                            println("$e")
                            Toast.makeText(activity as Context,e.toString(),Toast.LENGTH_LONG).show()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(activity as Context,"Error Occured while fetching data",Toast.LENGTH_SHORT).show()
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

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.home_page,menu)
            val searchitem=menu.findItem(R.id.search)
        if (searchitem!=null){
            val searchView=searchitem.actionView as SearchView

            searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotEmpty()){
                        resinfolist.clear()
                        val search=newText.toLowerCase()
                        reslist.forEach {
                            if(it.resname.toLowerCase().contains(search)){
                                resinfolist.add(it)
                               }
                            else{
                                no_res.visibility=View.VISIBLE
                            }

                        }
                        homeRecyclerAdapter!!.notifyDataSetChanged()


                    }else
                    {   resinfolist.clear()
                        resinfolist.addAll(reslist)
                        homeRecyclerAdapter!!.notifyDataSetChanged()
                    }
                    if(!resinfolist.isEmpty()){
                        no_res.visibility=View.GONE
                    }

                    return true
                }
            })}
        return super.onCreateOptionsMenu(menu,inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item?.itemId

        var sortby= arrayOf("rating","alphabet (A to Z)","alphabet (Z to A)","cost (low to high)","cost (high to low)")
        when(id){

            R.id.sort->{
                val sortbuilder=AlertDialog.Builder(activity as Context)
                sortbuilder.setTitle("Sort By").setSingleChoiceItems(sortby,itemselected, { dialog, which ->
                    itemselected=which
                })
                sortbuilder.setIcon(R.drawable.ic_baseline_sort_24)
                sortbuilder.setPositiveButton("ok",{text,listener->

                        when(itemselected){
                            0->{//Sort Restraunt Name by Rating
                                Collections.sort(resinfolist,ratingcomparator)
                                resinfolist.reverse()
                                progresslayout.visibility=View.VISIBLE
                                progressBar.visibility=View.VISIBLE
                                progtext.visibility=View.VISIBLE
                            }
                            1->{//Sort Restraunt Name by alphabet in ascending order
                                Collections.sort(resinfolist,alphabetcomparator)
                                progresslayout.visibility=View.VISIBLE
                                progressBar.visibility=View.VISIBLE
                                progtext.visibility=View.VISIBLE
                            }
                            2->{//Sort Restraunt Name by alphabet in descending order
                                Collections.sort(resinfolist,alphabetcomparator)
                                resinfolist.reverse()
                                progresslayout.visibility=View.VISIBLE
                                progressBar.visibility=View.VISIBLE
                                progtext.visibility=View.VISIBLE
                            }
                            3->{
                                Collections.sort(resinfolist,costcomparator)
                                progresslayout.visibility=View.VISIBLE
                                progressBar.visibility=View.VISIBLE
                                progtext.visibility=View.VISIBLE
                            }
                            4->{
                                Collections.sort(resinfolist,costcomparator)
                                resinfolist.reverse()
                                progresslayout.visibility=View.VISIBLE
                                progressBar.visibility=View.VISIBLE
                                progtext.visibility=View.VISIBLE
                            }

                        }
                        homeRecyclerAdapter!!.notifyDataSetChanged()
                        progtext.visibility=View.GONE
                        progresslayout.visibility=View.GONE
                        progressBar.visibility=View.GONE

                })

                sortbuilder.setNegativeButton("Cancel",{text,listener->

                })
                sortbuilder.create().show()
            }
                    }
                return super.onOptionsItemSelected(item)
        }


    }

