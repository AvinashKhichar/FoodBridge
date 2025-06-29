package com.example.foodbridge.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.foodbridge.R
import com.example.foodbridge.databinding.FragmentAccountOptionsBinding

class AccountOptionFragment : Fragment(R.layout.fragment_account_options) {
    private lateinit var binding: FragmentAccountOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?
    ): View?{
        binding = FragmentAccountOptionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        binding.registerButton.setOnClickListener{
            findNavController().navigate(R.id.action_accountOptionFragment_to_registerFragment)
        }

        binding.loginButton.setOnClickListener{
            findNavController().navigate(R.id.action_accountOptionFragment_to_loginFragment)
        }
    }
}