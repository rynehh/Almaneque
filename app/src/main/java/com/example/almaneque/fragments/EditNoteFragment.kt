package com.example.almaneque.fragments

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.almaneque.databinding.FragmentEditNoteBinding
import com.example.almaneque.factories.NoteViewModelFactory
import com.example.almaneque.models.notedata
import com.example.almaneque.viewmodels.NoteViewModel

class EditNoteFragment : Fragment() {

    private lateinit var binding: FragmentEditNoteBinding
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditNoteBinding.inflate(inflater, container, false)

        val factory = NoteViewModelFactory()
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        observarNota()
        setupButtonActions()

        return binding.root
    }

    private fun observarNota() {
        val noteId = arguments?.getInt("note_id") ?: return
        Log.d("EditNoteFragment", "ID de nota recibido: $noteId")
        noteViewModel.obtenerNota(noteId)

        noteViewModel.notaResponse.observe(viewLifecycleOwner) { nota ->
            if (nota != null) {
                Log.d("EditNoteFragment", "Nota obtenida: $nota")
                binding.titleEditText.setText(nota.titulo)
                binding.wateringEditText.setText(nota.regado)
                binding.lightEditText.setText(nota.luz)
                binding.fertilizationEditText.setText(nota.fertilizacion)
                binding.weatherEditText.setText(nota.clima)
                binding.extraInfoEditText.setText(nota.descripcion)
            } else {
                Log.e("EditNoteFragment", "Error: Nota no encontrada")
                Toast.makeText(requireContext(), "Error al cargar la nota", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupButtonActions() {
        // Configurar botón de retroceso
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Configurar botón para guardar cambios
        binding.btnCreatePost.setOnClickListener {
            if (validarCampos()) {
                val noteId = arguments?.getInt("note_id") ?: return@setOnClickListener
                val updatedNote = notedata(
                    id_nota = noteId,
                    titulo = binding.titleEditText.text.toString(),
                    descripcion = binding.extraInfoEditText.text.toString(),
                    luz = binding.lightEditText.text.toString(),
                    fertilizacion = binding.fertilizationEditText.text.toString(),
                    regado = binding.wateringEditText.text.toString(),
                    clima = binding.weatherEditText.text.toString()
                )
                noteViewModel.editarNota(updatedNote)
                Toast.makeText(requireContext(), "Nota actualizada correctamente", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
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
