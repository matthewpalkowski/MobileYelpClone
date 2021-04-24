package com.example.hw5_yelpclone

data class Business(
    val rating : Double,
    val price : String,
    val phone: String,
    val id: String,
    val alias: String,
    val is_closed: Boolean,
    val categories: List<Category>,
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

data class SupportedCategory(val title: String)

data class YelpCatList(val CatList: List<SupportedCategory>)