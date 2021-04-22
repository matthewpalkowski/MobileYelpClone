package com.example.hw5_yelpclone

data class Business(
    val rating : Double,
    val price : String,
    val phone: String,
    val id: String,
    val alias: String,
    val is_closed: Boolean,
    val category: List<Category>,
    val review_count : Int,
    val name: String,
    val url: String,
    val coordinates: Coordinates,
    val image_url: String,
    val location: Location,
    val distance: Double,
    val transactions: List<String>)

data class Category(val alias: String, val title: String)

data class Coordinates(val latitude: Double, val longitude: Double)

data class Location(
    val city: String,
    val country: String,
    val address1: String,
    val address2: String,
    val address3: String,
    val state: String,
    val zip_code: String)

data class SearchItem(val total: Int, val businesses : List<Business>)

//{
//    "total": 8228,
//    "businesses": [
//    {
//        "rating": 4,
//        "price": "$",
//        "phone": "+14152520800",
//        "id": "E8RJkjfdcwgtyoPMjQ_Olg",
//        "alias": "four-barrel-coffee-san-francisco",
//        "is_closed": false,
//        "categories": [
//        {
//            "alias": "coffee",
//            "title": "Coffee & Tea"
//        }
//        ],
//        "review_count": 1738,
//        "name": "Four Barrel Coffee",
//        "url": "https://www.yelp.com/biz/four-barrel-coffee-san-francisco",
//        "coordinates": {
//        "latitude": 37.7670169511878,
//        "longitude": -122.42184275
//    },
//        "image_url": "http://s3-media2.fl.yelpcdn.com/bphoto/MmgtASP3l_t4tPCL1iAsCg/o.jpg",
//        "location": {
//        "city": "San Francisco",
//        "country": "US",
//        "address2": "",
//        "address3": "",
//        "state": "CA",
//        "address1": "375 Valencia St",
//        "zip_code": "94103"
//    },
//        "distance": 1604.23,
//        "transactions": ["pickup", "delivery"]
//    },
//    // ...
//    ],
//    "region": {
//    "center": {
//        "latitude": 37.767413217936834,
//        "longitude": -122.42820739746094
//    }
//}
//}