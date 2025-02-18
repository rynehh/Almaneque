package com.example.almaneque.fragments

import NoteDatabaseHelper
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import com.example.almaneque.R
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import android.widget.ImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import androidx.recyclerview.widget.RecyclerView
import com.example.almaneque.adapters.PostAdapter
import com.example.almaneque.models.Post
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.ViewModelProvider
import com.example.almaneque.factories.PostViewModelFactory
import com.example.almaneque.factories.UsuarioViewModelFactory
import com.example.almaneque.viewmodels.PostViewModel
import com.example.almaneque.viewmodels.UsuarioViewModel


class HomeFragment : Fragment() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: PostAdapter
    private lateinit var usuarioViewModel: UsuarioViewModel
    private lateinit var postViewModel: PostViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recyclerView = view.findViewById<RecyclerView>(R.id.postsRecyclerView)
        val loadingSpinner = view.findViewById<ProgressBar>(R.id.loadingSpinner)

        val factory = UsuarioViewModelFactory()
        usuarioViewModel = ViewModelProvider(this, factory)[UsuarioViewModel::class.java]

        observarUsuarioBasico()
        usuarioViewModel.obtenerInfoBasicaUsuario()

        val postFactory = PostViewModelFactory()
        postViewModel = ViewModelProvider(this, postFactory)[PostViewModel::class.java]

        setupViews(view)

        postViewModel.publicacionesResponse.observe(viewLifecycleOwner) { posts ->
            if (posts != null && posts.isNotEmpty()) {

                loadingSpinner.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.updatePosts(posts)
            } else {

                loadingSpinner.visibility = View.GONE
                Toast.makeText(requireContext(), "No se pudieron cargar los posts", Toast.LENGTH_SHORT).show()
            }
        }

        setupRecyclerView(view)
        setupNavigationDrawer(view)
        setupFilters(view)
    }

    private fun isConnected(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
    private fun setupViews(view: View) {
        drawerLayout = view.findViewById(R.id.drawerLayout)

        // Configurar botón de menú
        view.findViewById<ImageView>(R.id.btnMenu).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        // Configurar botón de retroceso
        view.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            findNavController().navigateUp()
        }

        val fabHome = view.findViewById<FloatingActionButton>(R.id.fabCreatePost)


        if (!isConnected()) {
            // Deshabilitar el FAB y mostrar mensaje en modo sin conexión
            fabHome.isEnabled = false
            Toast.makeText(requireContext(), "Modo sin conexión: No puedes crear publicaciones", Toast.LENGTH_SHORT).show()
        } else {
            // Configurar el FAB para mostrar el menú contextual en modo conectado
            fabHome.setOnClickListener {
                showFabMenu(it)
            }
        }


    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.postsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        adapter = PostAdapter(mutableListOf())
        recyclerView.adapter = adapter


        val postViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        // Observa los datos del ViewModel
        postViewModel.publicacionesResponse.observe(viewLifecycleOwner) { publicaciones ->
            if (publicaciones != null) {
                adapter.updatePosts(publicaciones)
            }
        }


        postViewModel.obtenerPublicaciones()
    }

    private fun setupFilters(view: View) {
        val searchInput = view.findViewById<EditText>(R.id.searchBar)
        val ordenAlfabeticoSpinner = view.findViewById<Spinner>(R.id.sortSpinner)
        val categoriaSpinner = view.findViewById<Spinner>(R.id.categorySpinner)

        // Observa cambios en el cuadro de búsqueda
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                postViewModel.actualizarFiltros(nombre = query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val ordenAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Sin filtro", "Cronológico", "Orden alfabético")
        )
        ordenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ordenAlfabeticoSpinner.adapter = ordenAdapter

        val categoriaAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Todas las categorías", "Cuidado", "Interiores", "Crecimiento", "Decoracion",
                    "Condiciones", "Registro Personal", "Plagas", "Soluciones",
                    "Diseño y creatividad")
        )
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriaSpinner.adapter = categoriaAdapter

        ordenAlfabeticoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val ordenSeleccionado = when (position) {
                    0 -> "" // Sin filtro
                    1 -> "cronologico" // Cronológico
                    2 -> "abc" // Orden alfabético
                    else -> ""
                }
                postViewModel.actualizarFiltros(orden = ordenSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Observa cambios en la categoría
        categoriaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val categoriaSeleccionada = if (position == 0) null else position
                postViewModel.actualizarFiltros(categoria = categoriaSeleccionada)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun setupNavigationDrawer(view: View) {
        val navigationView = view.findViewById<NavigationView>(R.id.navigationView)

        // Configurar los ítems del menú
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.nav_profile -> {
                    if (!isConnected()) {
                        Toast.makeText(requireContext(), "No se puede acceder al perfil en modo sin conexión", Toast.LENGTH_SHORT).show()
                        false
                    } else {
                        findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                        true
                    }
                }
                R.id.nav_notes -> {
                    findNavController().navigate(R.id.action_homeFragment_to_noteFragment)
                    true
                }
                R.id.nav_logout -> {
                    usuarioViewModel.cerrarSesion()
                    val dbHelper = NoteDatabaseHelper(requireContext())
                    dbHelper.clearNotes()
                    findNavController().navigate(R.id.action_homeFragment_to_logInFragment)
                    true
                }
                else -> false

            }
        }

        // Configurar el botón de cerrar dentro del Drawer
        val headerView = navigationView.getHeaderView(0)
        val closeDrawerButton = headerView.findViewById<ImageView>(R.id.closeDrawerButton)
        closeDrawerButton.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.END)
        }
    }

    private fun showFabMenu(view: View) {
        val wrapper = ContextThemeWrapper(requireContext(), R.style.CenteredPopupMenuText);
        val popupMenu = PopupMenu(wrapper, view)
        popupMenu.menuInflater.inflate(R.menu.post_menu, popupMenu.menu)

        // Configura el listener para manejar clics en el menú
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_create_post -> {
                    // Acción para crear un post
                    findNavController().navigate(R.id.action_homeFragment_to_createPostFragment)
                    true
                }
                R.id.action_drafts -> {
                    // Acción para subir una foto
                    findNavController().navigate(R.id.action_homeFragment_to_draftFragment)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun observarUsuarioBasico() {
        usuarioViewModel.usuarioBasicoResponse.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status == "success") {
                val navigationView = requireView().findViewById<NavigationView>(R.id.navigationView)
                val headerView = navigationView.getHeaderView(0)

                // Actualizar el nombre de usuario
                val usernameTextView = headerView.findViewById<TextView>(R.id.usernameTV)
                usernameTextView.text = response.data?.nickname

                // Actualizar el avatar
                val avatarImageView = headerView.findViewById<ImageView>(R.id.profile_image)
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