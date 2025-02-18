package com.example.almaneque.fragments

import NoteDatabaseHelper
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.almaneque.R
import com.example.almaneque.adapters.NoteAdapter
import com.example.almaneque.factories.NoteViewModelFactory
import com.example.almaneque.viewmodels.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NoteFragment : Fragment() {

    private lateinit var adapter: NoteAdapter
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val factory = NoteViewModelFactory()
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        setupViews(view)
        setupRecyclerView(view)
        observeNotes()


        if (isConnected()) {
            observeNotes()
            noteViewModel.obtenerNotas()
        } else {
            showOfflineNotes()
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun showOfflineNotes() {
        val dbHelper = NoteDatabaseHelper(requireContext())
        val offlineNotes = dbHelper.getNotes()
        adapter.updateNotes(offlineNotes)
        Toast.makeText(requireContext(), "Modo sin conexión: solo lectura", Toast.LENGTH_SHORT).show()
    }

    private fun setupViews(view: View) {

        view.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            findNavController().navigateUp()
        }

        // Configurar el FAB para mostrar el menú contextual
        val fabCreateNote = view.findViewById<FloatingActionButton>(R.id.fabCreateNote)

        if (!isConnected()) {
            // Deshabilitar el FAB y mostrar mensaje en modo sin conexión
            fabCreateNote.isEnabled = false
            Toast.makeText(requireContext(), "Modo sin conexión: No se pueden crear notas", Toast.LENGTH_SHORT).show()
        } else {
            // Configurar el FAB para mostrar el menú contextual en modo conectado
            fabCreateNote.setOnClickListener {
                showFabMenu(it)
            }
        }

    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.noteRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Configurar el adaptador con el callback para el menú de opciones
        adapter = NoteAdapter(mutableListOf()) { actionId, note ->
            when (actionId) {
                R.id.action_edit_note -> {

                    val bundle = Bundle()
                    bundle.putInt("note_id", note.id_nota)
                    findNavController().navigate(R.id.action_noteFragment_to_editNoteFragment, bundle)

                }
                R.id.action_delete_note -> {
                    noteViewModel.eliminarNota(note.id_nota)
                }
            }
        }
        recyclerView.adapter = adapter
    }

    private fun observeNotes() {
        // Observa las notas en el ViewModel y actualiza el RecyclerView
        noteViewModel.notasResponse.observe(viewLifecycleOwner) { notas ->
            if (notas != null) {
                adapter.updateNotes(notas)

                //AQUI MANDAMOS A USAR LA BASE DE DATOS LOCAL DE SQL LITE
                val dbHelper = NoteDatabaseHelper(requireContext())
                notas.forEach { dbHelper.saveNoteIfNotExists(it) }
            }
        }

        noteViewModel.eliminarNotaResponse.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status == "success") {
                noteViewModel.obtenerNotas() // Recargar las notas
            } else {
                Toast.makeText(requireContext(), response?.message ?: "Error al eliminar nota", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showFabMenu(view: View) {
        val wrapper = ContextThemeWrapper(requireContext(), R.style.CenteredPopupMenuText)
        val popupMenu = PopupMenu(wrapper, view)
        popupMenu.menuInflater.inflate(R.menu.note_create_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_create_note -> {
                    // Navegar al fragmento para crear una nueva nota
                    findNavController().navigate(R.id.action_noteFragment_to_createNoteFragment)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}
