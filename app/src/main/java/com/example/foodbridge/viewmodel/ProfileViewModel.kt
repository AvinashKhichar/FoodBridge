package com.example.foodbridge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val firebaseAuth : FirebaseAuth,
    private val db : FirebaseFirestore
): ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val _profileData = MutableLiveData<ProfileData>()
    val profileData: LiveData<ProfileData> = _profileData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    data class ProfileData(
        val name: String = "",
        val email: String = "",
        val location: String = "",
        val donationAmount: String = ""
    )

    fun fetchUserProfile() {
        currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val profileData = ProfileData(
                            name = document.getString("name") ?: "",
                            email = document.getString("email") ?: "",
                            location = document.getString("location") ?: "",
                            donationAmount = document.getString("donationAmount") ?: ""
                        )
                        _profileData.value = profileData
                    } else {
                        _errorMessage.value = "User profile not found"
                    }
                }
                .addOnFailureListener { exception ->
                    _errorMessage.value = "Error fetching profile: ${exception.localizedMessage}"
                }
        } ?: run {
            _errorMessage.value = "No authenticated user"
        }
    }

    fun updateUserProfile(
        name: String,
        email: String,
        location: String,
        donationAmount: String
    ) {
        currentUser?.uid?.let { userId ->
            val userUpdates = hashMapOf(
                "name" to name,
                "email" to email,
                "location" to location,
                "donationAmount" to donationAmount
            )

            firestore.collection("users").document(userId)
                .update(userUpdates as Map<String, Any>)
                .addOnSuccessListener {
                    // Profile updated successfully, you might want to refresh the data
                    fetchUserProfile()
                }
                .addOnFailureListener { exception ->
                    _errorMessage.value = "Error updating profile: ${exception.localizedMessage}"
                }
        }
    }
}