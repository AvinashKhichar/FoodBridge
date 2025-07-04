package com.example.foodbridge.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.foodbridge.data.User
import com.example.foodbridge.util.Constants.USER_COLLECTION
import com.example.foodbridge.util.RegisterFieldState
import com.example.foodbridge.util.RegisterValidation
import com.example.foodbridge.util.Resource
import com.example.foodbridge.util.validateEmail
import com.example.foodbridge.util.validateFirstName
import com.example.foodbridge.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val firebaseAuth : FirebaseAuth,
    private val db : FirebaseFirestore
):ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register : Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()

    fun crateAccountWithEmailAndPassword(user: User, password:String){

        if(checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resource.loading())

            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        saveUserInfo(it.uid, user)
                        _register.value = Resource.Success(user)
                    }
                }.addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        }else{
            val registerFieldState = RegisterFieldState(
                validateFirstName(user.firstName), validateEmail(user.email), validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldState)
            }

        }
    }

    private fun saveUserInfo(userUid : String, user: User) {
        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String): Boolean {
        val nameValidation = validateFirstName(name = user.firstName)
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister = emailValidation is RegisterValidation.success &&
                passwordValidation is RegisterValidation.success

        return shouldRegister
    }
}