package com.example.almaneque.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.almaneque.R
import com.example.almaneque.databinding.FragmentCreateAccountBinding
import com.example.almaneque.factories.UsuarioViewModelFactory
import com.example.almaneque.models.Usuario
import com.example.almaneque.viewmodels.UsuarioViewModel

class CreateAccountFragment : Fragment() {

    //El uso de binding nos ayuda a no tener que estar inflando la vista
    //No tenemos que estar referenciado a los id de los elementos
    //Remplazamos el uso de findviewbyid lo cual llega a ser mas tardado y mas lento
    //Nos referenciamos de aqui: https://developer.android.com/topic/libraries/view-binding?hl=es-419#kts
    private lateinit var binding: FragmentCreateAccountBinding
    private lateinit var usuarioViewModel: UsuarioViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_account, container, false)
        binding = FragmentCreateAccountBinding.inflate(inflater, container, false)

        val avatarOptions = arrayOf("Avatar 1", "Avatar 2", "Avatar 3", "Avatar 4", "Avatar 5", "Avatar 6")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, avatarOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAvatar.adapter = adapter

        val factory = UsuarioViewModelFactory()
        usuarioViewModel = ViewModelProvider(this, factory)[UsuarioViewModel::class.java]

        observarLiveData()

        //Aqui pondemos la seleccion de avatares
        //Estos son las imagenes que tenemos en el proyecto
        //Como avatares
        val avatarResources = listOf(
            R.drawable.avatar1, // Avatar 1
            R.drawable.avatar2, // Avatar 2
            R.drawable.avatar3, // Avatar 3
            R.drawable.avatar4, // Avatar 4
            R.drawable.avatar5, // Avatar 5
            R.drawable.avatar6  // Avatar 6
        )

        //Aqui vinculamos el spinner con el imageview para que se pueda seleccionar
        //los avatares
        // Configura el spinner y el cambio de imagen
        binding.avatarImage.setImageResource(avatarResources[0])
        binding.spinnerAvatar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.avatarImage.setImageResource(avatarResources[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.avatarImage.setImageResource(avatarResources[0])
            }
        }

        // Configurar el botón de retroceso
        binding.btnBack.setOnClickListener {
            // Esto regresará al fragmento anterior (LogInFragment)
            findNavController().navigateUp()

        }

        binding.btnCreateAccount.setOnClickListener {
            if (validarCampos()) {
                val avatarSelected = when (binding.spinnerAvatar.selectedItemPosition) {
                    0 -> "avatar_1"
                    1 -> "avatar_2"
                    2 -> "avatar_3"
                    3 -> "avatar_4"
                    4 -> "avatar_5"
                    5 -> "avatar_6"
                    else -> "avatar_1"
                }

                val usuario = Usuario(
                    email = binding.emailCA.text.toString(),
                    nombre = binding.nameCA.text.toString(),
                    apellido = binding.lastNameCA.text.toString(),
                    username = binding.usernameCA.text.toString(),
                    avatar = avatarSelected,
                    contrasena = binding.passwordCA.text.toString(),
                    telefono = binding.phoneCA.text.toString(),
                    direccion = binding.directionCA.text.toString(),
                )

                // Enviar usuario al ViewModel
                usuarioViewModel.registrarUsuario(usuario)
            }
        }

        return binding.root
    }

    private fun observarLiveData() {
        usuarioViewModel.registroResponse.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status == "success") {
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_createAccountFragment_to_logInFragment)
            } else {
                Toast.makeText(requireContext(), "Error en el registro: ${response?.message ?: "Desconocido"}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validarCampos(): Boolean {

        val email = binding.emailCA.text.toString()
        val name = binding.nameCA.text.toString()
        val lastName = binding.lastNameCA.text.toString()
        val username = binding.usernameCA.text.toString()
        val password = binding.passwordCA.text.toString()
        val phone = binding.phoneCA.text.toString()
        val direction = binding.directionCA.text.toString()
        val repeatPassword = binding.repeatPasswordCA.text.toString()

        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}\$"
        val passwordRegex = Regex(passwordPattern)

        return when {
            email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(requireContext(), "Por favor, ingresa un email válido.", Toast.LENGTH_SHORT).show()
                false
            }
            name.isEmpty() -> {
                Toast.makeText(requireContext(), "El nombre no puede estar vacío.", Toast.LENGTH_SHORT).show()
                false
            }
            lastName.isEmpty() -> {
                Toast.makeText(requireContext(), "El apellido no puede estar vacío.", Toast.LENGTH_SHORT).show()
                false
            }
            username.isEmpty() -> {
                Toast.makeText(requireContext(), "El nombre de usuario no puede estar vacío.", Toast.LENGTH_SHORT).show()
                false
            }
            direction.isEmpty() -> {
                Toast.makeText(requireContext(), "La dirección no puede estar vacía.", Toast.LENGTH_SHORT).show()
                false
            }
            phone.isEmpty() || !phone.matches("^\\d{10}\$".toRegex()) -> {
                Toast.makeText(requireContext(), "El teléfono debe tener 10 dígitos.", Toast.LENGTH_SHORT).show()
                false
            }
            password.isEmpty() || !passwordRegex.matches(password) -> {
                Toast.makeText(requireContext(), "La contraseña debe tener al menos 8 caracteres, " +
                        "incluir una mayúscula, una minúscula y un número.", Toast.LENGTH_SHORT).show()
                false
            }
            repeatPassword != password -> {
                Toast.makeText(requireContext(), "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

}