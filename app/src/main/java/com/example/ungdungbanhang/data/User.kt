package com.example.ungdungbanhang.data

data class User(
    val fistName : String,
    val lastName : String,
    val email : String,
    var imagePath : String = "",
){
    constructor() : this("","","","")

}
