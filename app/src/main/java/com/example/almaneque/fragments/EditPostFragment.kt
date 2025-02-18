package com.example.almaneque.fragments

import ImageCarouselAdapter
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.almaneque.databinding.FragmentEditPostBinding
import com.example.almaneque.factories.PostViewModelFactory
import com.example.almaneque.models.ArchivoRequest
import com.example.almaneque.models.archivo
import com.example.almaneque.models.postdata
import com.example.almaneque.viewmodels.PostViewModel


class EditPostFragment : Fragment() {

    private lateinit var binding: FragmentEditPostBinding
    private lateinit var postViewModel: PostViewModel
    private val listaArchivos = mutableListOf<archivo>()

    companion object {
        private const val TAG = "EditPostFragment"
        private const val REQUEST_CODE_GALERIA = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        val factory = PostViewModelFactory()
        postViewModel = ViewModelProvider(this, factory)[PostViewModel::class.java]

        observarPublicacion()
        setupButtonActions()
        return binding.root
    }

    private fun observarPublicacion() {
        val postId = requireArguments().getInt("post_id", -1)
        if (postId == -1) {
            Toast.makeText(requireContext(), "Error: No se recibió el ID de la publicación", Toast.LENGTH_SHORT).show()
            return
        }

        postViewModel.obtenerPublicacion(postId)
        postViewModel.publicacionResponse.observe(viewLifecycleOwner) { post ->
            if (post != null) {
                cargarDatosPublicacion(post)
            } else {
                Toast.makeText(requireContext(), "Error al cargar la publicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarDatosPublicacion(post: postdata) {
        binding.titleEditText.setText(post.titulo)
        binding.categorySpinner.setSelection(post.id_cate)
        binding.contentEditText.setText(post.contenido)

    }

    private fun setupButtonActions() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }



        binding.btnCreatePost.setOnClickListener {
            if (validarCampos()) {
                val postId = requireArguments().getInt("post_id")
                val titulo = binding.titleEditText.text.toString()
                val contenido = binding.contentEditText.text.toString()
                val categoria = binding.categorySpinner.selectedItemPosition

                val publicacionEditada = postdata(
                    id_pub = postId,
                    titulo = titulo,
                    contenido = contenido,
                    borrador = 0,
                    id_cate = categoria,
                    categoria_nombre = "",
                    archivos = listaArchivos
                )

                postViewModel.editarPublicacion(publicacionEditada)
                Toast.makeText(requireContext(), "Publicación actualizada correctamente", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun validarCampos(): Boolean {
        val titulo = binding.titleEditText.text.toString()
        val contenido = binding.contentEditText.text.toString()
        val categoria = binding.categorySpinner.selectedItemPosition

        return when {
            titulo.isEmpty() -> {
                Toast.makeText(requireContext(), "El título no puede estar vacío.", Toast.LENGTH_SHORT).show()
                false
            }
            contenido.isEmpty() -> {
                Toast.makeText(requireContext(), "El contenido no puede estar vacío.", Toast.LENGTH_SHORT).show()
                false
            }
            categoria == 0 -> {
                Toast.makeText(requireContext(), "Debes seleccionar una categoría.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }
}
