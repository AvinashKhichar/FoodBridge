package com.example.foodbridge.fragments.loginRegister

import android.content.Intent
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
import com.example.foodbridge.R
import com.example.foodbridge.activities.DonationActivity
import com.example.foodbridge.data.User
import com.example.foodbridge.databinding.FragmentRegisterBinding
import com.example.foodbridge.util.RegisterValidation
import com.example.foodbridge.util.Resource
import com.example.foodbridge.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment :Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registernow.setOnClickListener(){
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.apply {
            RegisterButton.setOnClickListener {
                val user = User(
                    edname.text.toString().trim(),
                    edLastname.text.toString().trim(),
                    edEmailLogin.text.toString().trim()
                )
                val password = edPassLogin.text.toString()
                viewModel.crateAccountWithEmailAndPassword(user, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resource.loading -> {


                    }

                    is Resource.Success -> {
                        Log.d("test", it.data.toString())
                        Intent(requireActivity(), DonationActivity::class.java).also {
                                intent -> intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    is Resource.Error -> {
                        Log.d(TAG, it.message.toString())
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()

                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->

                if (validation.name is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edname.apply {
                            requestFocus()
                            error = validation.name.message
                        }
                    }
                }
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edEmailLogin.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edPassLogin.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }

            }
        }
    }
}