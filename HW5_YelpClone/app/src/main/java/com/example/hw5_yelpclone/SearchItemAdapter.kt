package com.example.hw5_yelpclone

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

/**
 * Adapter class for SearchItem. Assigns values to components of SearchItem.
 */
class SearchItemAdapter(private val searchItems : List<Business>) :
    RecyclerView.Adapter<SearchItemAdapter.SearchItemViewHolder>() {

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
        if(currentItem.location.address2.isNotEmpty()) stringBuilder.append("\n" + currentItem.location.address2)
        if(currentItem.location.address3.isNotEmpty()) stringBuilder.append("\n" + currentItem.location.address3)
        stringBuilder.append("\n" + currentItem.location.city + ", " + currentItem.location.state)
        holder.txtAddress.text = stringBuilder.toString()

        holder.txtBusinessTitle.text = currentItem.category[0].title //TODO Potentially iterate through all categories?
        holder.txtBusinessType.text = currentItem.name

        (currentItem.distance.toString() + " " + Resources.getSystem()
            .getString((R.string.milage))).also { holder.txtDistance.text = it }
        holder.txtPrice.text = currentItem.price
        Picasso.get().load(currentItem.image_url).into(holder.imgBusiness)
        (currentItem.review_count.toString() + " " +
                Resources.getSystem().getString((R.string.reviewCount))).also { holder.txtReviewTotal.text = it }
        holder.rtgContentRating.rating = currentItem.rating.toFloat()
    }

    override fun getItemCount(): Int { return searchItems.size}
}