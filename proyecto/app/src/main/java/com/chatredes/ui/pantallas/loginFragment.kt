package com.chatredes.ui.pantallas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.chatredes.R
import com.chatredes.databinding.FragmentLoginBinding
import com.chatredes.ui.viewmodel.StatusApp
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
        viewModel.connect() // ViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        setListeners()
    }

    private fun setVisibility(visibility: Int) {
        binding.apply {
            textLogin.visibility = visibility
            ILCorreo.visibility = visibility
            ILContrasena.visibility = visibility
            btnIniciarSesion.visibility = visibility
        }
    }

    private fun setObservers() {
        viewModel.status.observe(viewLifecycleOwner, Observer {
            when(it){
                is StatusApp.Loading -> {
                    binding.pbLogin.visibility = View.VISIBLE
                    setVisibility(View.GONE)
                }
                is StatusApp.Default -> {
                    binding.pbLogin.visibility = View.GONE
                    setVisibility(View.VISIBLE)
                }
                is StatusApp.Success -> {
                    binding.pbLogin.visibility = View.GONE
                    setVisibility(View.VISIBLE)
                    binding.btnIniciarSesion.isEnabled = false
                    requireView().findNavController().navigate(
                        R.id.action_loginFragment_to_lstChatFragment
                    )
                }
                is StatusApp.Error -> {
                    binding.pbLogin.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
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