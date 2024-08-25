package com.chatredes.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.chatredes.databinding.DialogRegisterBinding
import com.chatredes.ui.viewmodel.StatusApp
import com.chatredes.ui.viewmodel.UserViewModel

class RegisterDialog(
    private val viewModel: UserViewModel
): DialogFragment() {

    private lateinit var binding: DialogRegisterBinding

    private var dialog: AlertDialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        binding = DialogRegisterBinding.inflate(LayoutInflater.from(context))

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
                    // todo
                }
                is StatusApp.Success -> {

                }
                is StatusApp.Error -> {
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is StatusApp.Default -> {
                    binding.buttonRegister.isEnabled = false
                    Toast.makeText(requireContext(), "Contacto agregado", Toast.LENGTH_SHORT).show()
                    dialog?.dismiss() // Cerrar el diálogo cuando el estado es Success
                }
            }
        }
    }

    private fun setListeners() {
        binding.buttonRegister.setOnClickListener {
            viewModel.registerAccount(
                binding.editTextUsername.text.toString(),
                binding.editTextPassword.text.toString()
            )
        }
    }
}