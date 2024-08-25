package com.chatredes.ui.pantallas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.chatredes.Chat
import com.chatredes.R
import com.chatredes.databinding.FragmentChatBinding
import com.chatredes.domain.models.Message
import com.chatredes.ui.adapter.MessageAdapter
import com.chatredes.ui.viewmodel.MessageViewModel
import com.chatredes.ui.viewmodel.StatusApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class chatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private val args: chatFragmentArgs by navArgs()
    private val ChatViewModel: MessageViewModel by viewModels()

    private val messages: MutableList<Message> = mutableListOf()
    private lateinit var adapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        setObservers()
        ChatViewModel.getMessages()
        setListener()
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                ChatViewModel.messages.collect { newMessages ->
                    adapter.submitList(newMessages)
                    binding.recyclerChat.scrollToPosition(messages.size-1)
                }
            }
        }

        ChatViewModel.status.observe(viewLifecycleOwner) { status ->
            when (status) {
                is StatusApp.Loading -> {
                    // Mostrar un indicador de carga si es necesario
                }
                is StatusApp.Success -> {
                    Toast.makeText(requireContext(), "Mensaje enviado", Toast.LENGTH_SHORT).show()
                    binding.ETMensaje.text?.clear()
                }
                is StatusApp.Error -> {
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun setUpRecyclerView() {
        if (!this::adapter.isInitialized) {
            adapter = MessageAdapter(messages)
            binding.apply {
                recyclerChat.layoutManager = LinearLayoutManager(requireContext())
                recyclerChat.adapter = adapter
            }
        } else {
            adapter.notifyDataSetChanged()
        }
    }


    private fun setListener() {
        binding.IBEnviar.setOnClickListener {
            binding.ETMensaje.text!!.clear()

            ChatViewModel.sendMessage(
                binding.ETMensaje.text.toString(),
                args.JID
            )
        }
    }
}