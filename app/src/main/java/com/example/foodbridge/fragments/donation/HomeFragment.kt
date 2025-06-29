package com.example.foodbridge.fragments.donation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodbridge.R
import com.example.foodbridge.activities.DonationActivity
import com.example.foodbridge.adapters.HomeViewPagerAdapter
import com.example.foodbridge.adapters.NearYouAdapter
import com.example.foodbridge.databinding.FragmentHomeBinding
import com.example.foodbridge.util.Resource
import com.example.foodbridge.viewmodel.MainCategoryViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest


private val TAG = "HomeFragment"
@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.fragment_home) {
    val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var NearYouAdapter : NearYouAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRv()
        lifecycleScope.launchWhenStarted{
            viewModel.specialProducts.collectLatest{
                when(it){
                    is Resource.Success -> {
                        NearYouAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(com.example.foodbridge.fragments.donation.TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.loading -> {
                        showLoading()
                    }

                    is Resource.Unspecified -> TODO()
                }
            }
        }
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }


    private fun setupSpecialProductsRv() {
        NearYouAdapter = NearYouAdapter()
        binding.rvnearYou.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = NearYouAdapter()
        }
    }
}