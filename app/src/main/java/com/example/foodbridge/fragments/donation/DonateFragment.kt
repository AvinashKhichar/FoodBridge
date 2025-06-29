package com.example.foodbridge.fragments.donation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.foodbridge.R
import com.example.foodbridge.databinding.FragmentDonateBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.location.GeocoderNominatim
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.util.UUID

class DonateFragment : Fragment(R.layout.fragment_donate) {
    private lateinit var binding: FragmentDonateBinding
    private val firestore = Firebase.firestore
    private val storage = Firebase.storage.reference
    private val auth = FirebaseAuth.getInstance()

    // Image selection variables
    private var selectedImages = mutableListOf<Uri>()

    // Location variables
    private lateinit var map: MapView
    private var selectedLocation: GeoPoint? = null
    private var locationMarker: Marker? = null
    private val PACKAGE_NAME = "com.example.foodbridge"
    private val geocoder by lazy { GeocoderNominatim(PACKAGE_NAME) }


    // Image selection result launcher
    private val selectImagesActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val intent = result.data

            // Clear previous selections
            selectedImages.clear()

            // Multiple images selected
            if (intent?.clipData != null) {
                val count = intent.clipData?.itemCount ?: 0
                (0 until count).forEach {
                    val imageUri = intent.clipData?.getItemAt(it)?.uri
                    imageUri?.let { selectedImages.add(it) }
                }
            }
            // Single image selected
            else {
                val imageUri = intent?.data
                imageUri?.let { selectedImages.add(it) }
            }

            // Update UI to show number of selected images
            updateImageSelectionUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup map
        setupMap()

        // Setup search functionality
        setupSearchView()

        //navigate to home
        binding.Arrow.setOnClickListener(){
            findNavController().navigate(R.id.action_donateFragment3_to_homeFragment32)
        }


        // Setup image picker button
        binding.buttonImagesPicker.setOnClickListener {
            openImagePicker()
        }

        binding.saveProduct.setOnClickListener {
            if (validateInput()) {
                authenticateAndUpload()
            } else {
                Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        selectImagesActivityResult.launch(intent)
    }

    private fun updateImageSelectionUI() {
        binding.tvSelectedImages.text = selectedImages.size.toString()
    }

    private fun setupMap() {
        map = binding.mapView
        map.setMultiTouchControls(true)

        val mapController = map.controller
        mapController.setZoom(15.0)

        // Default location (can be changed to your app's default location)
        val startPoint = GeoPoint(28.7041, 77.1025) // Default coordinates (Delhi, India)
        mapController.setCenter(startPoint)

        // Add tap listener to map
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {
                    selectedLocation = it
                    updateLocationMarker(it)
                    resolveAddressFromLocation(it)
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean = false
        }

        val eventsOverlay = MapEventsOverlay(mapEventsReceiver)
        map.overlays.add(eventsOverlay)
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) searchLocation(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    private fun searchLocation(locationName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Show loading indicator
                withContext(Dispatchers.Main) {
                    binding.locationProgressBar.visibility = View.VISIBLE
                }

                // Search for location using Nominatim geocoder
                val searchResults = geocoder.getFromLocationName(locationName, 1)

                withContext(Dispatchers.Main) {
                    binding.locationProgressBar.visibility = View.GONE

                    if (searchResults.isNotEmpty()) {
                        val location = searchResults[0]
                        val geoPoint = GeoPoint(location.latitude, location.longitude)

                        // Update map center
                        map.controller.setCenter(geoPoint)
                        map.controller.setZoom(16.0)

                        // Set the selected location
                        selectedLocation = geoPoint
                        updateLocationMarker(geoPoint)

                        // Display address information
                        val address = location.getAddressLine(0) ?: "Address not found"
                        binding.tvAddressInfo.text = address
                        binding.tvAddressInfo.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No location found for: $locationName",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.locationProgressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Error searching location: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun resolveAddressFromLocation(geoPoint: GeoPoint) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)

                withContext(Dispatchers.Main) {
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0].getAddressLine(0) ?: "Address not found"
                        binding.tvAddressInfo.text = address
                        binding.tvAddressInfo.visibility = View.VISIBLE
                    } else {
                        binding.tvAddressInfo.text = "Address not found"
                        binding.tvAddressInfo.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.tvAddressInfo.text = "Could not resolve address"
                    binding.tvAddressInfo.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateLocationMarker(geoPoint: GeoPoint) {
        // Remove existing marker if any
        locationMarker?.let {
            map.overlays.remove(it)
        }

        // Add new marker
        locationMarker = Marker(map).apply {
            position = geoPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Selected Location"
            snippet = "Lat: ${geoPoint.latitude}, Lon: ${geoPoint.longitude}"
        }

        map.overlays.add(locationMarker)
        map.invalidate() // Refresh map
    }

    private fun validateInput(): Boolean {
        return binding.edName.text.isNotEmpty() &&
                binding.edCategory.text.isNotEmpty() &&
                selectedLocation != null &&
                selectedImages.isNotEmpty()
    }

    private fun authenticateAndUpload() {
        lifecycleScope.launch {
            try {
                // Authenticate anonymously if not already authenticated
                val currentUser = auth.currentUser ?: run {
                    val authResult = auth.signInAnonymously().await()
                    authResult.user
                }

                currentUser?.let { user ->
                    // Upload images and save to Firestore
                    val imageUrls = uploadImages(user.uid)
                    saveProductToFirestore(user.uid, imageUrls)
                } ?: throw Exception("Authentication failed")

            } catch (e: Exception) {
                Log.e("Upload", "Error: ${e.localizedMessage}")
                Toast.makeText(requireContext(), "Upload failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun uploadImages(userId: String): List<String> {
        val imageUrls = mutableListOf<String>()

        for (uri in selectedImages) {
            val imageId = UUID.randomUUID().toString()
            val imageRef = storage.child("donations/$userId/$imageId.jpg")

            try {
                val uploadTask = imageRef.putFile(uri).await()
                val downloadUrl = imageRef.downloadUrl.await().toString()
                imageUrls.add(downloadUrl)
            } catch (e: Exception) {
                Log.e("ImageUpload", "Failed to upload image: ${e.localizedMessage}")
            }
        }

        return imageUrls
    }

    private suspend fun saveProductToFirestore(userId: String, imageUrls: List<String>) {
        withContext(Dispatchers.IO) {
            val donationData = hashMapOf(
                "userId" to userId,
                "name" to binding.edName.text.toString(),
                "category" to binding.edCategory.text.toString(),
                "images" to imageUrls,
                "location" to hashMapOf(
                    "latitude" to selectedLocation?.latitude,
                    "longitude" to selectedLocation?.longitude,
                    "address" to binding.tvAddressInfo.text.toString()
                ),
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection("Donations")
                .add(donationData)
                .await()

            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Donation uploaded successfully", Toast.LENGTH_SHORT).show()
                // Clear form after successful upload
                clearForm()
            }
        }
    }

    private fun clearForm() {
        // Clear input fields
        binding.edName.text.clear()
        binding.edCategory.text.clear()

        // Clear images
        selectedImages.clear()
        updateImageSelectionUI()

        // Clear location
        selectedLocation = null
        locationMarker?.let {
            map.overlays.remove(it)
            locationMarker = null
        }
        binding.tvAddressInfo.text = ""
        binding.tvAddressInfo.visibility = View.GONE

        // Reset map to default location
        val startPoint = GeoPoint(28.7041, 77.1025)
        map.controller.setCenter(startPoint)
        map.controller.setZoom(15.0)
        map.invalidate()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}