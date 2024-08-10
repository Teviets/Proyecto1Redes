package com.chatredes.ui.pantallas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chatredes.R
import com.chatredes.databinding.FragmentLoginBinding


class loginFragment : Fragment() {

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

        setListeners()
    }

    private fun setListeners() {
        binding.btnIniciarSesion.setOnClickListener{
            findNavController().navigate(
                R.id.action_loginFragment_to_chatFragment
            )
        }
    }


}