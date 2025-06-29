package com.example.foodbridge.util

sealed class Resource<T>(
    val data: T?=null,
    val message: String = null.toString()
){

    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(message:String): Resource<T>(message =message)
    class loading<T>():Resource<T>()
    class Unspecified<T>():Resource<T>()
}