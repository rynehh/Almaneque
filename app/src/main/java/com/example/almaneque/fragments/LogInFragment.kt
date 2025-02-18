package com.example.almaneque.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.almaneque.R
import com.example.almaneque.factories.UsuarioViewModelFactory
import com.example.almaneque.models.Credenciales
import com.example.almaneque.viewmodels.UsuarioViewModel
import android.content.Context
import android.content.SharedPreferences

class LogInFragment : Fragment() {

    private lateinit var usuarioViewModel: UsuarioViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_log_in, container, false)

        val factory = UsuarioViewModelFactory()
        usuarioViewModel = ViewModelProvider(this, factory)[UsuarioViewModel::class.java]

        // Observa los LiveData
        // Live data es una clase de Android Studio que se usa para observar datos de manera reactiva
        // Se utiliza mucho para el modelo MVVC que estamos aplicando y para poder actualizar la UI
        // Ya que el viewmodel no tiene conocimiento directo del fragment.
        //Referencia de LiveData: https://developer.android.com/topic/libraries/architecture/livedata
        observarLiveData()

        // Asegúrate de que este ID coincida con el de tu TextView en fragment_log_in.xml
        view.findViewById<TextView>(R.id.tvCreateAccount).setOnClickListener {
            try {
                findNavController().navigate(R.id.action_logInFragment_to_createAccountFragment)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        view.findViewById<Button>(R.id.btnLogIn).setOnClickListener {
            // Aqui manejamos el inicio de sesion con el ViewModel
            val username = view.findViewById<EditText>(R.id.edtUsername).text.toString()
            val password = view.findViewById<EditText>(R.id.edtPassword).text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                val credenciales = Credenciales(username, password)
                usuarioViewModel.iniciarSesion(credenciales)
            } else {
                Toast.makeText(context, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun observarLiveData() {
        usuarioViewModel.loginResponse.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status == "success") {

                Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show()
                // Navegar a HomeFragment después del inicio de sesión exitoso
                findNavController().navigate(R.id.action_logInFragment_to_homeFragment)
            } else {
                Toast.makeText(context, "Error en el inicio de sesión", Toast.LENGTH_SHORT).show()
            }
        }
    }



}