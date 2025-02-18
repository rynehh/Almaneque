package com.example.almaneque.fragments

import ImageCarouselAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.almaneque.R
import com.example.almaneque.databinding.FragmentCreatePostBinding
import com.example.almaneque.factories.PostViewModelFactory
import com.example.almaneque.models.ArchivoRequest
import com.example.almaneque.models.Post
import com.example.almaneque.viewmodels.PostViewModel
import com.google.gson.Gson

class CreatePostFragment : Fragment() {

    private lateinit var binding: FragmentCreatePostBinding
    private lateinit var postViewModel: PostViewModel
    private val listaArchivos = mutableListOf<ArchivoRequest>()


    companion object {
        private const val REQUEST_CODE_GALERIA = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = PostViewModelFactory()
        postViewModel = ViewModelProvider(this, factory)[PostViewModel::class.java]

        setupSpinner()
        setupObservers()
        setupButtonActions()


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                showExitAlert()
            }
        })
    }

    private fun showExitAlert() {
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Advertencia")
            .setMessage("Estás a punto de perder tus datos. Guarda tu publicación como borrador para no perderlos.")
            .setPositiveButton("Continuar") { dialog, _ ->
                dialog.dismiss()

                findNavController().navigateUp()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()

            }
            .create()

        alertDialog.show()
    }

    private fun setupSpinner() {
        val categorias = listOf("Selecciona Categoria", "Cuidado", "Interiores", "Crecimiento", "Decoracion",
            "Condiciones", "Registro Personal", "Plagas", "Soluciones",
            "Diseño y creatividad")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter
    }

    private fun setupObservers() {
        postViewModel.crearPublicacionResponse.observe(viewLifecycleOwner) { response ->
            if (response.status == "success") {
                Toast.makeText(requireContext(), "Publicación creada con éxito", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp() // Vuelve a la pantalla anterior
            } else {
                Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupButtonActions() {
        // Configurar botón de retroceso
        binding.btnBack.setOnClickListener {
            if (validarCampos()) {
                val titulo = binding.titleEditText.text.toString()
                val contenido = binding.contentEditText.text.toString()
                val categoria = binding.categorySpinner.selectedItemPosition

                Log.d("ListaArchivos", listaArchivos.toString())

                val publicacion = Post(

                    titulo = titulo,
                    contenido = contenido,
                    borrador = true,
                    id_cate = categoria,
                    archivos = listaArchivos

                )

                Log.d("JSON Request", Gson().toJson(publicacion))

                postViewModel.crearPublicacion(publicacion)
                Toast.makeText(requireContext(), "Borrador guardado", Toast.LENGTH_SHORT).show()
            }

            findNavController().navigateUp()
        }

        binding.selectImageButton.setOnClickListener {
            abrirGaleria()
        }

        // Configura el boton de crear una pulicacion
        // Aqui volvemos a usar el binding por mejor practica, investigando nos dimos cuenta
        // que es buena idea usarlo en toda la estructura del codigo, sin embargo no es necesario
        // en lugares donde el find view es muy basico
        // Aqui ayuda mucho por el manejo de la insercion a la base de datos

        binding.btnCreatePost.setOnClickListener {
            if (validarCampos()) {
                val titulo = binding.titleEditText.text.toString()
                val contenido = binding.contentEditText.text.toString()
                val categoria = binding.categorySpinner.selectedItemPosition

                Log.d("ListaArchivos", listaArchivos.toString())

                val publicacion = Post(

                    titulo = titulo,
                    contenido = contenido,
                    borrador = false,
                    id_cate = categoria,
                    archivos = listaArchivos

                )

                Log.d("JSON Request", Gson().toJson(publicacion))

                postViewModel.crearPublicacion(publicacion)
            }
        }
    }

    private fun actualizarCarruselDeImagenes() {
        val base64Images = listaArchivos.map { it.contenido ?: "" }
        val adapter = ImageCarouselAdapter(base64Images)
        binding.imageCarousel.adapter = adapter
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

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, REQUEST_CODE_GALERIA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALERIA && resultCode == Activity.RESULT_OK) {
            val selectedImages = mutableListOf<ArchivoRequest>()

            if (data?.clipData != null) {

                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    selectedImages.add(procesarImagen(imageUri))
                }
            } else if (data?.data != null) {

                val imageUri = data.data!!
                selectedImages.add(procesarImagen(imageUri))
            }


            listaArchivos.addAll(selectedImages)
            Log.d("ListaArchivos", "Después de agregar imágenes: $listaArchivos")
            actualizarCarruselDeImagenes()
        }
    }

    private fun procesarImagen(uri: Uri): ArchivoRequest {
        val stream = requireContext().contentResolver.openInputStream(uri)
        val bytes = stream!!.readBytes() ?: byteArrayOf()
        val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)

        val archivo = ArchivoRequest(base64)
        Log.d("ArchivoRequest", archivo.toString())
        return archivo
    }



}
