package com.example.foodbridge.util

sealed class RegisterValidation() {
    object success:RegisterValidation()
    data class Failed(val message:String):RegisterValidation()
}

data class RegisterFieldState(
    val name: RegisterValidation,
    val email:RegisterValidation,
    val password: RegisterValidation
)