package com.example.almaneque.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.almaneque.R
import com.example.almaneque.adapters.ProfilePostAdapter
import com.example.almaneque.factories.PostViewModelFactory
import com.example.almaneque.factories.ProfileViewModelFactory
import com.example.almaneque.factories.UsuarioViewModelFactory
import com.example.almaneque.viewmodels.PostViewModel
import com.example.almaneque.viewmodels.ProfileViewModel
import com.example.almaneque.viewmodels.UsuarioViewModel

class ProfileFragment : Fragment() {
    private lateinit var adapter: ProfilePostAdapter
    private lateinit var usuarioViewModel: UsuarioViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var postViewModel: PostViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val factory = ProfileViewModelFactory()
        profileViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        val factory2 = UsuarioViewModelFactory()
        usuarioViewModel = ViewModelProvider(this, factory2)[UsuarioViewModel::class.java]

        val factory3 = PostViewModelFactory()
        postViewModel = ViewModelProvider(this, factory3)[PostViewModel::class.java]

        usuarioViewModel.obtenerInfoBasicaUsuario()
        observarUsuarioBasico(view)
        setupViews(view)


        setupRecyclerView(view)
        setupFilters(view)
    }

    private fun setupViews(view: View) {
        // Configurar botón para editar perfil
        view.findViewById<ImageView>(R.id.profile_image).setOnClickListener {


            //val bundle = Bundle()
            //bundle.putInt("note_id", note.id_nota), logica en dado caso se actualize en base
            // a un id, pero usamos la sesion activa
            findNavController().navigate(R.id.action_profileFragment_to_editAccountFragment)
        }

        // Configurar botón de retroceso
        view.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.profilePostsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        adapter = ProfilePostAdapter(mutableListOf()) { actionId, post ->
            when (actionId) {
                R.id.action_edit_post -> {
                    val bundle = Bundle().apply {
                        putInt("post_id", post.id_pub)
                    }
                    Log.d("ProfileFragment", "Navegando a EditPostFragment con post_id=${post.id_pub}")
                    findNavController().navigate(R.id.action_profileFragment_to_editPostFragment, bundle)
                }
                R.id.action_delete_post -> {
                    postViewModel.eliminarPublicacion(post.id_pub)

                }
            }
        }
        recyclerView.adapter = adapter


        profileViewModel.publicacionesResponse.observe(viewLifecycleOwner) { publicaciones ->
            if (publicaciones != null) {
                adapter.updatePosts(publicaciones)
            }
        }
        postViewModel.publicacionesResponse.observe(viewLifecycleOwner) { publicaciones ->
            adapter.updatePosts(publicaciones)
        }


        profileViewModel.obtenerMisPublicaciones()
    }

    private fun setupFilters(view: View) {
        val searchInput = view.findViewById<EditText>(R.id.searchBarProfile)
        val ordenSpinner = view.findViewById<Spinner>(R.id.sortSpinnerProfile)
        val categoriaSpinner = view.findViewById<Spinner>(R.id.categorySpinnerProfile)


        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                profileViewModel.actualizarFiltros(nombre = query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        val ordenAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Sin filtro", "Cronológico", "Orden alfabético")
        )
        ordenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ordenSpinner.adapter = ordenAdapter

        val categoriaAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Todas las categorías", "Cuidado", "Interiores", "Crecimiento", "Decoración",
                "Condiciones", "Registro Personal", "Plagas", "Soluciones",
                "Diseño y creatividad")
        )
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriaSpinner.adapter = categoriaAdapter


        ordenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val ordenSeleccionado = when (position) {
                    0 -> ""
                    1 -> "cronologico"
                    2 -> "abc"
                    else -> ""
                }
                profileViewModel.actualizarFiltros(orden = ordenSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        categoriaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val categoriaSeleccionada = if (position == 0) null else position
                profileViewModel.actualizarFiltros(categoria = categoriaSeleccionada)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun observarUsuarioBasico(view: View) {
        usuarioViewModel.usuarioBasicoResponse.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status == "success") {

                val usernameTextView = view.findViewById<TextView>(R.id.usernameP)
                usernameTextView.text = response.data?.nickname


                val avatarImageView = view.findViewById<ImageView>(R.id.profile_image)
                val avatarResource = when (response.data?.avatar) {
                    "avatar_1" -> R.drawable.avatar1
                    "avatar_2" -> R.drawable.avatar2
                    "avatar_3" -> R.drawable.avatar3
                    "avatar_4" -> R.drawable.avatar4
                    "avatar_5" -> R.drawable.avatar5
                    "avatar_6" -> R.drawable.avatar6
                    else -> R.drawable.avatar1
                }
                avatarImageView.setImageResource(avatarResource)
            } else {
                Toast.makeText(requireContext(), "Error al cargar la información del usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
