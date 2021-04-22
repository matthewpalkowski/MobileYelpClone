package com.example.hw5_yelpclone

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
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Class that manages the behavior of MainActivity.
 * @author Matthew Palkowski
 */
class MainActivity : AppCompatActivity() {

    /* TODO - Requirements
     *  -Connection to Yelp API to get restaurants and businesses in the area
     *      -Use Retrofit Web API (add library to manifest)
     *      -Use GET request with the following base URL https://api.yelp.com/v3/businesses/search
     *      -See help info at https://www.yelp.com/developers/documentation/v3/business_search
     *      -Get API Key from https://www.yelp.com/developers/documentation/v3/authentication
     *  -Show results in a RecyclerView, items should take the following form
     *      -name
     *      -image
     *          -Use Picasso Library to DL image from img url from YELP Web API
     *      -rating
     *      -reviews
     *      -distance from location in miles
     *      -address
     *      -Price ($,$$,$$$, etc.)
     *  -Widgets to take in 'business/food' and location
     *  -Handle Empty Strings and display alert dialog with the error
     *  -Hide Keyboard after entering text
     */

    /* TODO - Derived TODOs
    *   -Set up RecyclerView visibility
    *   -Check for WIFI
    *       -Throw error message permission is denied
    *       -Throw error message when no connection to internet
    *   -Check for API connection
    *       -Throw error when connection to API can't be made
    *   -Parse return from yelp and assign it to elements of recycler view item
    */

    private val TAG = "MainActivity"
    private val BASE_URL = "https://api.yelp.com/v3/"
    private val CLIENT_ID = "UB8h9ifG_nTPj3nB6HRSNQ" //TODO maybe delete
    private val API_KEY = "DrzLl84iZlolxCOKKVHXNHD50mrRT-BMxIUz7XiW86URPbRvPclVUNrdzdhAoBxlJ53TZVhqf_l7bvoawN_EmLZt06SZLBbxAVBUm4SVjXI0LyWRh5-FchVBerN8YHYx"

    private lateinit var adapter: SearchItemAdapter

    private lateinit var recyclerView : RecyclerView

    private lateinit var listener : View.OnClickListener
    private lateinit var focusListener : View.OnFocusChangeListener

    private lateinit var contentEditText : EditText
    private lateinit var locationEditText: EditText

    private lateinit var businessList: ArrayList<Business>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        businessList = ArrayList<Business>()

        recyclerView = findViewById(R.id.recyclerSearchResults)
        adapter = SearchItemAdapter(businessList)
        recyclerView.adapter = adapter

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

    private fun showAlert(alertMessage : String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.alertTitle))
        builder.setMessage(alertMessage)
        builder.setIcon(android.R.drawable.ic_delete)
        builder.setPositiveButton("OK"){ dialogInterface: DialogInterface, i: Int -> }
        val dialog = builder.create()
        dialog.show()
    }

    private fun submitQuery(content: String, location: String)
    {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val yelpAPI = retrofit.create(IYelpService::class.java)

        //TODO Change content and location to function inputs
        yelpAPI.getSearchItems("Bearer $API_KEY", "pizza"," New Britain").enqueue(object : Callback<SearchItem> {
            override fun onResponse(call: Call<SearchItem>, response: Response<SearchItem>) {
                businessList.addAll(response.body()!!.businesses)
                adapter.notifyDataSetChanged()
                recyclerView.visibility = View.VISIBLE
                Log.d("test","test")
            }

            override fun onFailure(call: Call<SearchItem>, t: Throwable) {
                //TODO Display alert
                Log.d(TAG, "onFailure: $t")
            }
        })
    }

    inner class ButtonListener : View.OnClickListener {
        override fun onClick(v: View?) {
            v!!.hideKeyboard()
            var message = ""
            val contentEditText : EditText = findViewById(R.id.editTxtContentID)
            val locationEditText : EditText = findViewById(R.id.editTxtLocationID)

            if(contentEditText.text.isEmpty() ||
                    contentEditText.text.toString() == getString(R.string.contentDefault))
                message = getString(R.string.seachTermEmpty)

            if(locationEditText.text.isEmpty() ||
                    locationEditText.text.toString() == getString(R.string.cityIDDefault)) {
                if (message.length > 1) message += "\n\n"
                message += getString(R.string.locationEmpty)
            }

            if(message.isNotEmpty()) showAlert(message)

            else submitQuery(contentEditText.text.toString(),locationEditText.text.toString())
        }
    }

    inner class FocusListener : View.OnFocusChangeListener{
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            val searchText : String = (v as EditText).text.toString()
            if(hasFocus &&
                    ((searchText == getString(R.string.cityIDDefault)) ||
                            (searchText == getText(R.string.contentDefault)))) {
                v.text.clear()
            }

            if (searchText.isEmpty() && !hasFocus) {
                if(v.id == findViewById<EditText>(R.id.editTxtContentID).id){
                    v .setText(getString(R.string.contentDefault))

                }

                if (v.id == findViewById<EditText>(R.id.editTxtLocationID).id){
                    v.setText(getString(R.string.cityIDDefault))

                }
            }

            setTextColor()
        }

    }
}