package com.example.foodbridge.data

data class User(
    val firstName : String,
    val lastName : String,
    val email : String,
    var imagePath:String=""

){
    constructor(): this("","","","")
}
