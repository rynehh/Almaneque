package com.example.almaneque.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.almaneque.R
import com.example.almaneque.databinding.FragmentEditAccountBinding
import com.example.almaneque.factories.UsuarioViewModelFactory
import com.example.almaneque.models.alluserdata
import com.example.almaneque.models.userupdate
import com.example.almaneque.viewmodels.UsuarioViewModel

class EditAccountFragment : Fragment() {

    private lateinit var binding: FragmentEditAccountBinding
    private lateinit var usuarioViewModel: UsuarioViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditAccountBinding.inflate(inflater, container, false)

        val avatarOptions =
            arrayOf("Avatar 1", "Avatar 2", "Avatar 3", "Avatar 4", "Avatar 5", "Avatar 6")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, avatarOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAvatar.adapter = adapter

        val factory = UsuarioViewModelFactory()
        usuarioViewModel = ViewModelProvider(this, factory)[UsuarioViewModel::class.java]

        observarLiveData()
        usuarioViewModel.obtenerInfoCompletaUsuario()

        // Vincular spinner con avatares
        val avatarResources = listOf(
            R.drawable.avatar1,
            R.drawable.avatar2,
            R.drawable.avatar3,
            R.drawable.avatar4,
            R.drawable.avatar5,
            R.drawable.avatar6
        )
        binding.spinnerAvatar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.avatarImage.setImageResource(avatarResources[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.avatarImage.setImageResource(avatarResources[0])
            }
        }

        // Configurar botón de guardar cambios
        binding.btnCreateAccount.setOnClickListener {
            if (validarCampos()) {
                val usuarioActualizado = obtenerUsuarioActualizado()
                usuarioViewModel.actualizarUsuario(usuarioActualizado)
            }
        }

        // Configurar botón de retroceso
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun observarLiveData() {
        usuarioViewModel.usuarioCompletoResponse.observe(viewLifecycleOwner) { usuario ->
            if (usuario != null) {
                cargarDatosUsuario(usuario)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error al cargar la información del usuario",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        usuarioViewModel.actualizacionResultado.observe(viewLifecycleOwner) { respuesta ->
            if (respuesta != null && respuesta.status == "success") {
                Toast.makeText(
                    requireContext(),
                    "Perfil actualizado correctamente",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(
                    requireContext(),
                    respuesta?.message ?: "Error al actualizar el perfil",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun cargarDatosUsuario(usuario: alluserdata) {
        binding.usernameCA.setText(usuario.nickname)
        binding.nameCA.setText(usuario.nombre)
        binding.lastNameCA.setText(usuario.apellido)
        binding.phoneCA.setText(usuario.telefono)
        binding.directionCA.setText(usuario.direccion)

        // Seleccionar avatar en el spinner
        val avatarIndex = when (usuario.avatar) {
            "avatar_1" -> 0
            "avatar_2" -> 1
            "avatar_3" -> 2
            "avatar_4" -> 3
            "avatar_5" -> 4
            "avatar_6" -> 5
            else -> 0
        }
        binding.spinnerAvatar.setSelection(avatarIndex)
        binding.avatarImage.setImageResource(
            when (avatarIndex) {
                0 -> R.drawable.avatar1
                1 -> R.drawable.avatar2
                2 -> R.drawable.avatar3
                3 -> R.drawable.avatar4
                4 -> R.drawable.avatar5
                5 -> R.drawable.avatar6
                else -> R.drawable.avatar1
            }
        )
    }

    private fun validarCampos(): Boolean {
        val username = binding.usernameCA.text.toString()
        val name = binding.nameCA.text.toString()
        val lastName = binding.lastNameCA.text.toString()
        val phone = binding.phoneCA.text.toString()
        val direction = binding.directionCA.text.toString()
        val password = binding.passwordCA.text.toString()
        val repeatPassword = binding.repeatPasswordCA.text.toString()

        // Expresión regular para validar la contraseña
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"
        val passwordRegex = Regex(passwordPattern)

        // Validar los campos
        return when {
            username.isEmpty() -> {
                Toast.makeText(
                    requireContext(),
                    "El nombre de usuario no puede estar vacío.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            name.isEmpty() -> {
                Toast.makeText(
                    requireContext(),
                    "El nombre no puede estar vacío.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            lastName.isEmpty() -> {
                Toast.makeText(
                    requireContext(),
                    "El apellido no puede estar vacío.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            phone.isEmpty() || !phone.matches("^\\d{10}\$".toRegex()) -> {
                Toast.makeText(
                    requireContext(),
                    "El teléfono debe tener 10 dígitos.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            direction.isEmpty() -> {
                Toast.makeText(
                    requireContext(),
                    "La dirección no puede estar vacía.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            password.isNotEmpty() && !passwordRegex.matches(password) -> {
                Toast.makeText(
                    requireContext(),
                    "La contraseña debe tener al menos 8 caracteres, incluir una mayúscula, una minúscula y un número.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            password.isNotEmpty() && repeatPassword != password -> {
                Toast.makeText(
                    requireContext(),
                    "Las contraseñas no coinciden.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            else -> true
        }
    }

    private fun obtenerUsuarioActualizado(): userupdate {
        val username = binding.usernameCA.text.toString()
        val name = binding.nameCA.text.toString()
        val lastName = binding.lastNameCA.text.toString()
        val phone = binding.phoneCA.text.toString()
        val direction = binding.directionCA.text.toString()
        val password = binding.passwordCA.text.toString() // Puede estar vacío

        val avatarSelected = when (binding.spinnerAvatar.selectedItemPosition) {
            0 -> "avatar_1"
            1 -> "avatar_2"
            2 -> "avatar_3"
            3 -> "avatar_4"
            4 -> "avatar_5"
            5 -> "avatar_6"
            else -> "avatar_1"
        }

        return userupdate(
            nickname = username,
            avatar = avatarSelected,
            nombre = name,
            apellido = lastName,
            telefono = phone,
            direccion = direction,
            contrasena = if (password.isNotEmpty()) password else ""
        )
    }
}
