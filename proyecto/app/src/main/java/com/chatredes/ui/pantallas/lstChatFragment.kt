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
import com.chatredes.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

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


        val manager = SessionManager(requireContext())
        Toast.makeText(requireContext(), "Bienvenido ${manager.getUserDetails()["username"]}", Toast.LENGTH_SHORT).show()
        setObservers()
        setListeners()
        viewModel.getContacts()
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                R.id.logout -> {
                    val manager = SessionManager(requireContext())
                    manager.logoutUser()
                    requireView().findNavController().navigate(R.id.loginFragment)
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
        viewModel.contacts.observe(viewLifecycleOwner, Observer{
            contacts = it
            setUpRecyclerView()
        })
    }

    private fun setUpRecyclerView() {
        if(!this::adapter.isInitialized){
            adapter = ContactAdapter(contacts, this)
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