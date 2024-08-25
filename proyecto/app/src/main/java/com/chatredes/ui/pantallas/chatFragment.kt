package com.chatredes.ui.pantallas

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.chatredes.databinding.FragmentChatBinding
import com.chatredes.domain.models.Message
import com.chatredes.ui.adapter.MessageAdapter
import com.chatredes.ui.viewmodel.MessageViewModel
import com.chatredes.ui.viewmodel.StatusApp
import dagger.hilt.android.AndroidEntryPoint

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

        setObservers()
        setListener()
    }

    private fun setObservers() {
        ChatViewModel.messages.observe(viewLifecycleOwner, Observer {
            Log.d("ChatFragment", "Messages observed: ${messages.size} messages")
            setUpRecyclerView(it.filter { msg -> msg.receiver == args.JID || msg.sender == args.JID })
        })
        ChatViewModel.status.observe(viewLifecycleOwner, Observer {
            when (it) {
                is StatusApp.Loading -> {
                    // todo proximo
                }

                is StatusApp.Default -> {
                    // todo proximo
                }

                is StatusApp.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is StatusApp.Success -> {
                    // todo proximo
                    Toast.makeText(requireContext(), "Mensaje enviado", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setUpRecyclerView(msg: List<Message>) {
        Log.d("ChatFragment", "Setting up RecyclerView with ${msg.size} messages")
        if (!this::adapter.isInitialized) {
            adapter = MessageAdapter(msg.toMutableList())
            binding.recyclerChat.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerChat.adapter = adapter
        } else {
            adapter.updateMessages(msg)
        }
    }




    private fun setListener() {
        binding.IBEnviar.setOnClickListener {

            Log.d("ChatFragment", "Sending message: ${binding.ETMensaje.text}")
            ChatViewModel.sendMessage(
                binding.ETMensaje.text.toString(),
                args.JID
            )
            binding.ETMensaje.text!!.clear()
        }
    }
}