package com.example.almaneque.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.almaneque.databinding.FragmentCreateNoteBinding
import com.example.almaneque.factories.NoteViewModelFactory
import com.example.almaneque.models.Note
import com.example.almaneque.viewmodels.NoteViewModel

class CreateNoteFragment : Fragment() {

    private lateinit var binding: FragmentCreateNoteBinding
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateNoteBinding.inflate(inflater, container, false)

        val factory = NoteViewModelFactory()
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        observarLiveData()
        setupButtonActions()

        return binding.root
    }

    private fun observarLiveData() {
        noteViewModel.crearNotaResponse.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status == "success") {
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Error al crear la nota: ${response?.message ?: "Desconocido"}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupButtonActions() {
        // Configurar botón de retroceso
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCreatePost.setOnClickListener {
            if (validarCampos()) {
                val note = Note(
                    titulo = binding.titleEditText.text.toString(),
                    regado = binding.wateringEditText.text.toString(),
                    luz = binding.lightEditText.text.toString(),
                    fertilizacion = binding.fertilizationEditText.text.toString(),
                    clima = binding.weatherEditText.text.toString(),
                    descripcion = binding.extraInfoEditText.text.toString()
                )
                noteViewModel.crearNota(note)
            }
        }
    }

    private fun validarCampos(): Boolean {
        val titulo = binding.titleEditText.text.toString()
        val regado = binding.wateringEditText.text.toString()
        val luz = binding.lightEditText.text.toString()
        val fertilizacion = binding.fertilizationEditText.text.toString()
        val clima = binding.weatherEditText.text.toString()
        val descripcion = binding.extraInfoEditText.text.toString()

        return when {
            titulo.isEmpty() -> {
                Toast.makeText(requireContext(), "El título no puede estar vacío.", Toast.LENGTH_SHORT).show()
                false
            }
            regado.isEmpty() -> {
                Toast.makeText(requireContext(), "El campo de regado no puede estar vacío.", Toast.LENGTH_SHORT).show()
                false
            }
            luz.isEmpty() -> {
                Toast.makeText(requireContext(), "El campo de luz no puede estar vacío.", Toast.LENGTH_SHORT).show()
                false
            }
            fertilizacion.isEmpty() -> {
                Toast.makeText(requireContext(), "El campo de fertilización no puede estar vacío.", Toast.LENGTH_SHORT).show()
                false
            }
            clima.isEmpty() -> {
                Toast.makeText(requireContext(), "El campo de clima no puede estar vacío.", Toast.LENGTH_SHORT).show()
                false
            }
            descripcion.isEmpty() -> {
                Toast.makeText(requireContext(), "La descripción no puede estar vacía.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }
}
