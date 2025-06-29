package com.example.foodbridge.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.foodbridge.R
import com.example.foodbridge.databinding.FragmentProfileBinding
import com.example.foodbridge.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe profile data
        viewModel.profileData.observe(viewLifecycleOwner) { profileData ->
            binding.edUser.setText(profileData.name)
            binding.edEmail.setText(profileData.email)
            binding.edloc.setText(profileData.location)
            binding.edDonation.setText(profileData.donationAmount)
        }

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Set up change button click listener
        binding.change.setOnClickListener {
            updateProfile()
        }

        // Fetch user profile when fragment is created
        viewModel.fetchUserProfile()
    }

    private fun updateProfile() {
        val name = binding.edUser.text.toString()
        val email = binding.edEmail.text.toString()
        val location = binding.edloc.text.toString()
        val donationAmount = binding.edDonation.text.toString()

        viewModel.updateUserProfile(name, email, location, donationAmount)
    }
}