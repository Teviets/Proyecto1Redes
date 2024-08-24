package com.chatredes.ui.pantallas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.chatredes.R
import com.chatredes.databinding.FragmentLstChatBinding
import com.chatredes.ui.viewmodel.ContactViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class lstChatFragment : Fragment() {

    private val viewModel: ContactViewModel by viewModels()

    private lateinit var binding: FragmentLstChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLstChatBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }


}