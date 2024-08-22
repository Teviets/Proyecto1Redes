package com.chatredes.ui.pantallas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.chatredes.R
import com.chatredes.databinding.FragmentLoginBinding
import com.chatredes.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class loginFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels() // ViewModel

    private lateinit var binding: FragmentLoginBinding // binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false) // binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        setListeners()
    }

    private fun setObservers() {
        viewModel.isLogged.observe(viewLifecycleOwner, Observer{
            if (it) {
                findNavController().navigate(R.id.action_loginFragment_to_chatFragment)
            }
        })
    }

    private fun setListeners() {
        binding.btnIniciarSesion.setOnClickListener{
            val username = binding.etCorreo.text.toString()
            val password = binding.etContrasena.text.toString()

            if (username.isNotBlank() && password.isNotBlank()) {
                viewModel.login(username, password)
            }
        }
    }


}