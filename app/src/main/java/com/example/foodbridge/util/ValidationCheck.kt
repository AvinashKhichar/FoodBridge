package com.example.foodbridge.util

import android.util.Patterns
import java.util.regex.Pattern

fun validateFirstName(name: String): RegisterValidation{
    if(name.isEmpty())
        return RegisterValidation.Failed("First name cannot be empty")

    return RegisterValidation.success
}

fun validateEmail(email: String): RegisterValidation{
    if(email.isEmpty())
            return RegisterValidation.Failed("Email cannot be empty")

    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Wrong Email format")

    return RegisterValidation.success
}

fun validatePassword(password: String): RegisterValidation{
    if(password.isEmpty())
        return RegisterValidation.Failed("Password cannot be empty")

    if(password.length < 6)
        return RegisterValidation.Failed("Password should contain 6 char")

    return RegisterValidation.success
}