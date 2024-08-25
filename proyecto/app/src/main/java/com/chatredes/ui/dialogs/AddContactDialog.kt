package com.chatredes.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.chatredes.databinding.DialogAddContactBinding
import com.chatredes.ui.viewmodel.ContactViewModel
import com.chatredes.ui.viewmodel.StatusApp

class AddContactDialog (private val viewModel: ContactViewModel): DialogFragment() {

    private lateinit var binding: DialogAddContactBinding

    private var dialog: AlertDialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        binding = DialogAddContactBinding.inflate(LayoutInflater.from(context))

        dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setCancelable(false)
            .create().apply {
                setListeners()
            }

        setUpObservers()

        return dialog!!
    }

    private fun setUpObservers() {
        viewModel.status.observe(this) { status ->
            when (status) {
                is StatusApp.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    setVisiblity(View.GONE)
                }
                is StatusApp.Success -> {
                    binding.progressBar.visibility = View.GONE
                    setVisiblity(View.VISIBLE)
                    binding.buttonAddContact.isEnabled = false
                    binding.buttonCancel.isEnabled = false
                    Toast.makeText(requireContext(), "Contacto agregado", Toast.LENGTH_SHORT).show()
                    dialog?.dismiss() // Cerrar el diálogo cuando el estado es Success
                }
                is StatusApp.Error -> {
                    binding.progressBar.visibility = View.GONE
                    setVisiblity(View.VISIBLE)
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is StatusApp.Default -> {
                    setVisiblity(View.VISIBLE)
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun setVisiblity(visibility: Int) {
        binding.buttonAddContact.visibility = visibility
        binding.buttonCancel.visibility = visibility
        binding.editTextContactName.visibility = visibility
        binding.editTextContactJID.visibility = visibility
        binding.dialogTitle.visibility = visibility
    }

    private fun setListeners() {
        binding.buttonAddContact.setOnClickListener {
            val contactName = binding.editTextContactName.editText?.text.toString()
            val contactJID = binding.editTextContactJID.editText?.text.toString()

            if (contactName.isNotBlank() && contactJID.isNotBlank()) {
                viewModel.addContact(contactJID+"@alumchat.lol", contactName)
            }
        }
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
    }
}
