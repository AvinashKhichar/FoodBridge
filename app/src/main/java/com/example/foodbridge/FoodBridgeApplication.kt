package com.example.foodbridge

import android.app.Application
import android.util.Log
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FoodBridgeApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        // If using App Check
        val appCheck = FirebaseAppCheck.getInstance()
        appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        try {
            ProviderInstaller.installIfNeeded(this)
        } catch (e: GooglePlayServicesRepairableException) {
            // Google Play services is out of date, show dialog to let user update
            // You can use GoogleApiAvailability.getInstance().getErrorDialog() to show a dialog
            Log.e("Security", "Google Play Services is repairable", e)
        } catch (e: GooglePlayServicesNotAvailableException) {
            // Google Play services is not available on this device
            Log.e("Security", "Google Play Services not available", e)
        }
    }
}