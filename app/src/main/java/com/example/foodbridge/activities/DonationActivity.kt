package com.example.foodbridge.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.foodbridge.R
import com.example.foodbridge.databinding.ActivityDonationBinding
import com.example.foodbridge.viewmodel.ProfileViewModel
import com.example.foodbridge.viewmodel.RegisterViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DonationActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityDonationBinding.inflate(layoutInflater)
    }

    private val profileViewModel by viewModels<ProfileViewModel>()


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as androidx.navigation.fragment.NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setupWithNavController(navController)
    }
}