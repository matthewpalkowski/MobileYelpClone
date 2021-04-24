package com.example.hw5_yelpclone

import android.app.Application
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.system.exitProcess

/**
 * Class that manages the behavior of MainActivity.
 * @author Matthew Palkowski
 */
class MainActivity : AppCompatActivity() {

    /* TODO Mark Down extra credit
     *  -No data available note for price
     *  -Category validation
     */

    //TODO Set up connection to the categories end-point and validate user input against it
    //FIXME Disallow new line characters in the search boxes
    //FIXME Look into the max 20 results for any search

    private val TAG = "MainActivity"

    private val BASE_URL = "https://api.yelp.com/v3/"
    private val CATEGORY_URL= "https://www.yelp.com/developers/documentation/v3/all_category_list/categories.json/"
    private val API_KEY = "DrzLl84iZlolxCOKKVHXNHD50mrRT-BMxIUz7XiW86URPbRvPclVUNrdzdhAoBxlJ53TZVhqf_l7bvoawN_EmLZt06SZLBbxAVBUm4SVjXI0LyWRh5-FchVBerN8YHYx"
    private val CLIENT_ID = "UB8h9ifG_nTPj3nB6HRSNQ"

    private lateinit var adapter: SearchItemAdapter

    private lateinit var recyclerView : RecyclerView

    private lateinit var listener : View.OnClickListener
    private lateinit var focusListener : View.OnFocusChangeListener

    private lateinit var contentEditText : EditText
    private lateinit var locationEditText: EditText

    private lateinit var businessList: ArrayList<Business>
    private lateinit var categorySet: HashSet<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        businessList = ArrayList()
        categorySet = HashSet()
        retrieveCategories()

        adapter = SearchItemAdapter(businessList)
        recyclerView = findViewById(R.id.recyclerSearchResults)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        listener = ButtonListener()
        findViewById<Button>(R.id.button)!!.setOnClickListener(listener)

        contentEditText = findViewById(R.id.editTxtContentID)
        locationEditText = findViewById(R.id.editTxtLocationID)

        focusListener = FocusListener()
        contentEditText.onFocusChangeListener = focusListener
        locationEditText.onFocusChangeListener = focusListener

        setTextColor()
    }

    private fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun setTextColor(){
        if(contentEditText.text.toString() == getString(R.string.contentDefault))
            contentEditText.setTextColor(ContextCompat.getColor(applicationContext,R.color.suggestionColor))
        else
            contentEditText.setTextColor(ContextCompat.getColor(applicationContext,R.color.black))

        if(locationEditText.text.toString() == getString(R.string.cityIDDefault))
            locationEditText.setTextColor(ContextCompat.getColor(applicationContext,R.color.suggestionColor))
        else
            locationEditText.setTextColor(ContextCompat.getColor(applicationContext,R.color.black))
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

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val yelpAPI = retrofit.create(IYelpService::class.java)

        yelpAPI.getSearchItems("Bearer $API_KEY", content,location).enqueue(object : Callback<SearchItem> {
            override fun onResponse(call: Call<SearchItem>, response: Response<SearchItem>) {
                businessList.addAll(response.body()!!.businesses)
                adapter.notifyDataSetChanged()
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

        yelpAPI.getCategories().enqueue(object : Callback<YelpCatList> {
            override fun onResponse(call: Call<YelpCatList>, response: Response<YelpCatList>) {
                if (!response.body()!!.CatList.isNullOrEmpty()) {
                    for (yelpCat in response.body()!!.CatList)
                        categorySet.add(yelpCat.title)
                }
            }

            override fun onFailure(call: Call<YelpCatList>, t: Throwable) {
                Log.d(TAG, "onFailure: $t" )
                showAlert(getString(R.string.categoryFailure),true)
            }
        })
    }

    inner class ButtonListener : View.OnClickListener {
        override fun onClick(v: View?) {
            v!!.hideKeyboard()
            var message = ""
            val contentEditText : EditText = findViewById(R.id.editTxtContentID)
            val locationEditText : EditText = findViewById(R.id.editTxtLocationID)

            if(contentEditText.text.isBlank() ||
                    contentEditText.text.toString() == getString(R.string.contentDefault))
                message = getString(R.string.seachTermEmpty)

            if(locationEditText.text.isBlank() ||
                    locationEditText.text.toString() == getString(R.string.cityIDDefault)) {
                if (message.length > 1) message += "\n\n"
                message += getString(R.string.locationEmpty)
            }

            if(message.isNotBlank()) showAlert(message,false)

            else submitQuery(contentEditText.text.toString(),locationEditText.text.toString())
        }
    }

    inner class FocusListener : View.OnFocusChangeListener{
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            val searchText : String = (v as EditText).text.toString()
            if(hasFocus &&
                    ((searchText == getString(R.string.cityIDDefault)) ||
                            (searchText == getText(R.string.contentDefault))))
                                v.text.clear()


            if (searchText.isBlank() && !hasFocus) {
                if(v.id == findViewById<EditText>(R.id.editTxtContentID).id)
                    v .setText(getString(R.string.contentDefault))

                if (v.id == findViewById<EditText>(R.id.editTxtLocationID).id)
                    v.setText(getString(R.string.cityIDDefault))
            }
            setTextColor()
        }
    }
}