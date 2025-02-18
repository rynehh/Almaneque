package com.example.almaneque.adapters

import ImageCarouselAdapter
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.almaneque.R
import com.example.almaneque.databinding.ItemProfilePostBinding
import com.example.almaneque.models.postdata

class ProfilePostAdapter(
    private val posts: MutableList<postdata>,
    private val onPostOptionSelected: (actionId: Int, post: postdata) -> Unit
) : RecyclerView.Adapter<ProfilePostAdapter.ProfilePostViewHolder>() {

    inner class ProfilePostViewHolder(private val binding: ItemProfilePostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: postdata) {
            binding.apply {
                titleText.text = post.titulo
                categoryText.text = post.categoria_nombre
                contentText.text = post.contenido

                // Configuración del carrusel de imágenes
                val base64Images = post.archivos.map { it.archivo }
                val adapter = ImageCarouselAdapter(base64Images)
                postImageCarousel.adapter = adapter

                // Configuración del botón de opciones
                optionsButton.setOnClickListener { showPostMenu(it, post) }
            }
        }

        private fun showPostMenu(view: View, post: postdata) {
            val wrapper = ContextThemeWrapper(view.context, R.style.CenteredPopupMenuText)
            val popupMenu = PopupMenu(wrapper, view)
            popupMenu.menuInflater.inflate(R.menu.profile_post_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                onPostOptionSelected(menuItem.itemId, post)
                true
            }
            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePostViewHolder {
        val binding = ItemProfilePostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfilePostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfilePostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    // Método para actualizar los datos del adaptador
    fun updatePosts(newPosts: List<postdata>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }
}
