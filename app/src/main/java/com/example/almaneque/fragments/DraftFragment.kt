package com.example.almaneque.fragments

import DraftAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.almaneque.R
import com.example.almaneque.factories.PostViewModelFactory
import com.example.almaneque.models.Borrador
import com.example.almaneque.viewmodels.PostViewModel

class DraftFragment : Fragment() {

    private lateinit var adapter: DraftAdapter
    private lateinit var postViewModel: PostViewModel
    private val draftList = mutableListOf<Borrador>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_draft, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Crear el ViewModel usando un Factory
        val factory = PostViewModelFactory()
        postViewModel = ViewModelProvider(this, factory)[PostViewModel::class.java]

        setupViews(view)
        setupRecyclerView(view)
        observeDrafts()

        // Cargar los borradores desde el ViewModel
        postViewModel.listarBorradores()
    }

    private fun setupViews(view: View) {
        view.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.draftRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inicializar el adaptador
        adapter = DraftAdapter(draftList,
            onEditClick = { draft ->
                val bundle = Bundle()
                bundle.putInt("post_id", draft.id_pub)
                findNavController().navigate(R.id.action_draftFragment_to_editPostFragment, bundle)
            },
            onDeleteClick = { draft ->
                postViewModel.eliminarBorrador(draft.id_pub)
                draftList.remove(draft)
                adapter.notifyDataSetChanged()
            }
        )

        recyclerView.adapter = adapter
    }



    private fun observeDrafts() {

        postViewModel.borradoresResponse.observe(viewLifecycleOwner) { borradores ->
            if (borradores != null) {
                adapter.updateDrafts(borradores)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error al cargar los borradores",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        postViewModel.eliminarBorradorResponse.observe(viewLifecycleOwner) { response ->
            if (response?.status == "success") {
                postViewModel.listarBorradores()
            } else {
                Toast.makeText(
                    requireContext(),
                    response?.message ?: "Error al eliminar borrador",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}

