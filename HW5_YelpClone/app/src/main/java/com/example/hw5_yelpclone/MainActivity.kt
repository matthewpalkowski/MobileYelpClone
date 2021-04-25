package com.example.hw5_yelpclone

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.system.exitProcess

/**
 * Class that manages the behavior of MainActivity. Retrieves data from yelp endpoints regarding
 * characteristics of businesses that match the class of business queried by the user located near
 * a location specified by the user.
 * @author Matthew Palkowski
 */
class MainActivity : AppCompatActivity() {

    private val BASE_URL = "https://api.yelp.com/v3/"
    private val CATEGORY_URL= "https://www.yelp.com/developers/documentation/v3/all_category_list/categories.json/"
    private val API_KEY = "DrzLl84iZlolxCOKKVHXNHD50mrRT-BMxIUz7XiW86URPbRvPclVUNrdzdhAoBxlJ53TZVhqf_l7bvoawN_EmLZt06SZLBbxAVBUm4SVjXI0LyWRh5-FchVBerN8YHYx"
    private val BEARER = "Bearer"

    private lateinit var adapter: SearchItemAdapter
    private lateinit var autoCompleteAdapter : ArrayAdapter<String>

    private lateinit var recyclerView : RecyclerView

    private lateinit var listener : View.OnClickListener

    private lateinit var contentEditText : AutoCompleteTextView

    private lateinit var locationEditText: EditText

    private lateinit var noResults : TextView

    private lateinit var businessList: ArrayList<Business>
    private lateinit var categoryList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        businessList = ArrayList()
        categoryList = ArrayList()
        retrieveCategories()

        adapter = SearchItemAdapter(businessList)
        recyclerView = findViewById(R.id.recyclerSearchResults)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        listener = ButtonListener()
        findViewById<Button>(R.id.button)!!.setOnClickListener(listener)

        contentEditText = findViewById(R.id.autoTxtCategory)
        autoCompleteAdapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, categoryList)
        contentEditText.setAdapter(autoCompleteAdapter)

        noResults = findViewById(R.id.txtNoResults)
        noResults.visibility = View.INVISIBLE

        locationEditText = findViewById(R.id.editTxtLocationID)
    }

    private fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun showAlert(alertMessage : String, terminalPoint: Boolean){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.alertTitle))
        builder.setMessage(alertMessage)
        builder.setIcon(android.R.drawable.ic_delete)
        if(terminalPoint)
            builder.setPositiveButton(getString(R.string.Ok) ){ _: DialogInterface, _: Int ->
                exitProcess(-1)}
        else
            builder.setPositiveButton(getString(R.string.Ok)){ _: DialogInterface, _: Int -> }
        val dialog = builder.create()
        dialog.show()
    }

    private fun submitQuery(content: String, location: String)
    {
        businessList.clear()
        noResults.visibility = View.INVISIBLE

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val yelpAPI = retrofit.create(IYelpService::class.java)

        yelpAPI.getSearchItems("$BEARER $API_KEY", content,location).enqueue(object : Callback<SearchItem> {
            override fun onResponse(call: Call<SearchItem>, response: Response<SearchItem>) {
                if(response.body()?.businesses.isNullOrEmpty()) noResults.visibility = View.VISIBLE
                else{
                    businessList.addAll(response.body()!!.businesses)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<SearchItem>, t: Throwable) {
                showAlert(getString(R.string.apiFailureAlert),false)
            }
        })
    }

    private fun retrieveCategories() {
        val retrofit = Retrofit.Builder()
                .baseUrl(CATEGORY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val yelpAPI = retrofit.create(IYelpService::class.java)

        yelpAPI.getCategories().enqueue(object : Callback<List<SupportedCategory>> {
            override fun onResponse(
                call: Call<List<SupportedCategory>>,
                response: Response<List<SupportedCategory>>
            ) {
                for (i in response.body()!!.indices)
                    categoryList.add(response.body()!![i].title)
            }

            override fun onFailure(call: Call<List<SupportedCategory>>, t: Throwable) {
                showAlert(getString(R.string.categoryFailure), true)
            }
        })
    }

    inner class ButtonListener : View.OnClickListener {
        override fun onClick(v: View?) {
            v!!.hideKeyboard()
            var message = ""
            if(contentEditText.text.isBlank())
                message = getString(R.string.seachTermEmpty)

            if(locationEditText.text.isBlank()) {
                if (message.length > 1) message += "\n\n"
                message += getString(R.string.locationEmpty)
            }

            if(message.isNotBlank()) showAlert(message,false)

            else submitQuery(contentEditText.text.toString(),locationEditText.text.toString())
        }
    }
}