package com.example.foodbridge.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.foodBridge.dialog.setupBottomSheetDialog
import com.example.foodbridge.R
import com.example.foodbridge.activities.DonationActivity
import com.example.foodbridge.databinding.FragmentLoginBinding
import com.example.foodbridge.util.Resource
import com.example.foodbridge.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registernow.setOnClickListener(){
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.apply{
            LoginButton.setOnClickListener{
                val email = edEmailLogin.text.toString().trim()
                val password = edPassLogin.text.toString()
                viewModel.login(email, password)

            }
        }

        binding.forgotPass.setOnClickListener(){
           setupBottomSheetDialog {email ->
                viewModel.resetPassword(email)
           }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect(){
                when(it){
                    is Resource.loading -> {

                    }
                    is Resource.Success -> {
                        Snackbar.make(requireView(),"Reset link was sent to your email", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(),"Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect(){
                when(it){
                    is Resource.loading -> {


                    }
                    is Resource.Success -> {

                        Intent(requireActivity(),DonationActivity::class.java).also {
                            intent -> intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

}