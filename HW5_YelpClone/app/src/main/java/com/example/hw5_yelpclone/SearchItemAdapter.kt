package com.example.hw5_yelpclone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.function.DoubleToLongFunction

/**
 * Adapter class for SearchItem. Assigns values to components of SearchItem.
 */
class SearchItemAdapter(private val searchItems : List<Business>) :
    RecyclerView.Adapter<SearchItemAdapter.SearchItemViewHolder>() {

    private val DISTANCE_SUFFIX : String = " mi"
    private val REVIEW_SUFFIX : String = " Reviews"
    private val NO_DATA : String = "No data available"
    private val METERS_TO_MILES : Double = 0.000621371

    class SearchItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var imgBusiness : ImageView = itemView.findViewById(R.id.imgBusinessThumbnail)
        var txtAddress : TextView = itemView.findViewById(R.id.txtAddress)
        val txtBusinessTitle : TextView = itemView.findViewById(R.id.txtBusinessTitle)
        val txtBusinessType : TextView = itemView.findViewById(R.id.txtBusinessType)
        val txtDistance : TextView = itemView.findViewById(R.id.txtDistance)
        val txtPrice : TextView = itemView.findViewById(R.id.txtPrice)
        val txtReviewTotal : TextView = itemView.findViewById(R.id.txtReviewTotal)
        val rtgContentRating : RatingBar = itemView.findViewById(R.id.rtgBarContentRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item_result, parent, false)
        return SearchItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val currentItem = searchItems[position]

        val stringBuilder: StringBuilder = StringBuilder()
        stringBuilder.append(currentItem.location.address1)
        stringBuilder.append("\n" + currentItem.location.city + ", " + currentItem.location.state)
        holder.txtAddress.text = stringBuilder.toString()
        stringBuilder.clear()

        holder.txtDistance.text = formatDistance(currentItem.distance)

        for(category in currentItem.categories){
            stringBuilder.append(category.title)
            if(category != currentItem.categories.last()) stringBuilder.append(", ")
        }
        holder.txtBusinessType.text = stringBuilder.toString()

        holder.txtBusinessTitle.text = currentItem.name


        if(!currentItem.price.isNullOrBlank()) holder.txtPrice.text = currentItem.price
        else holder.txtPrice.text = NO_DATA

        if(!currentItem.price.isNullOrBlank())
            Picasso.get().load(currentItem.image_url).into(holder.imgBusiness)
        else holder.imgBusiness.setImageResource(R.drawable.no_image_available)

        holder.txtReviewTotal.text = currentItem.review_count.toString() + REVIEW_SUFFIX

        holder.rtgContentRating.rating = currentItem.rating.toFloat()
    }

    override fun getItemCount(): Int { return searchItems.size}

    private fun formatDistance(meterDistance : Double?) : String{
        var formattedDistance : String
        if (meterDistance == null) formattedDistance = NO_DATA
        else{
            var mileDistance : Double = meterDistance * METERS_TO_MILES
            formattedDistance = String.format("%.2f",mileDistance) + DISTANCE_SUFFIX
        }
        return formattedDistance
    }
}