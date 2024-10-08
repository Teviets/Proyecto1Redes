package com.chatredes.ui.pantallas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.chatredes.R
import com.chatredes.data.constantes.SessionManager
import com.chatredes.databinding.FragmentLstChatBinding
import com.chatredes.domain.models.Contact
import com.chatredes.ui.adapter.ContactAdapter
import com.chatredes.ui.dialogs.AddContactDialog
import com.chatredes.ui.viewmodel.ContactViewModel
import com.chatredes.ui.viewmodel.StatusApp
import com.chatredes.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.jivesoftware.smack.packet.Presence

@AndroidEntryPoint
class lstChatFragment : Fragment(), ContactAdapter.RecyclerViewContactEvents {

    private val viewModel: ContactViewModel by viewModels()
    private val UserViewModel: UserViewModel by viewModels()

    private lateinit var binding: FragmentLstChatBinding

    private var contacts : List<Contact> = emptyList()

    private lateinit var adapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLstChatBinding.inflate(inflater, container, false)

        // Configura RecyclerView
        adapter = ContactAdapter(mutableListOf(), this)
        binding.recyclerChat.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = this@lstChatFragment.adapter
        }

        // Observadores y listeners
        setObservers()
        setListeners()

        // Obtiene los contactos
        viewModel.getContacts()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setListeners()
        viewModel.getContacts()
    }

    override fun onResume() {
        super.onResume()
        setObservers()
        setListeners()
        viewModel.getContacts()
    }

    override fun onStart() {
        super.onStart()
        setObservers()
        setListeners()
        viewModel.getContacts()
    }

    override fun onPause() {
        super.onPause()
        setObservers()
        setListeners()
        viewModel.getContacts()
    }

    override fun onStop() {
        super.onStop()
        setObservers()
        setListeners()
        viewModel.getContacts()
    }

    private fun setListeners() {
        binding.IBAdd.setOnClickListener {
            showAddContactDialog()
        }
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.action_logout -> {
                    UserViewModel.logout()
                    true
                }
                R.id.action_deleteaccount -> {
                    UserViewModel.deleteAccount()
                    true
                }
                R.id.action_disponiblity -> {
                    UserViewModel.changeDisponibility(Presence.Mode.chat)
                    true
                }
                R.id.action_status -> {
                    UserViewModel.changeDisponibility(Presence.Mode.away)
                    true
                }
                R.id.action_NDisponible -> {
                    UserViewModel.changeDisponibility(Presence.Mode.xa)
                    true
                }
                R.id.action_ocupado -> {
                    UserViewModel.changeDisponibility(Presence.Mode.dnd)
                    true
                }
                else -> false
            }
        }
    }

    private fun showAddContactDialog(){
        val dialog = AddContactDialog(viewModel)
        dialog.show(childFragmentManager, "AddContactDialog")
    }

    private fun setObservers() {
        viewModel.contacts.observe(viewLifecycleOwner, Observer { contactList ->
            // Actualiza la lista del adaptador y notifica los cambios
            adapter.updateContacts(contactList)
        })

        UserViewModel.status.observe(viewLifecycleOwner, Observer {
            when(it){
                is StatusApp.Loading -> {
                    // todo prograss bar
                }
                is StatusApp.Default -> {
                    // todo vista normal
                }
                is StatusApp.Error -> {
                    // todo mensaje de error
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is StatusApp.Success -> {
                    requireView().findNavController().navigate(
                        lstChatFragmentDirections.actionLstChatFragmentToLoginFragment()
                    )
                }
            }
        })
    }

    private fun setUpRecyclerView() {
        if(!this::adapter.isInitialized){
            adapter = ContactAdapter(contacts.toMutableList(), this)
            binding.apply {
                recyclerChat.layoutManager = LinearLayoutManager(requireContext())
                recyclerChat.setHasFixedSize(true)
                recyclerChat.adapter = adapter
            }
        }else{
            adapter.notifyDataSetChanged()
        }
    }

    override fun onContactClick(contact: Contact) {
        requireView().findNavController().navigate(
            lstChatFragmentDirections.actionLstChatFragmentToChatFragment(contact.username)
        )
    }


}