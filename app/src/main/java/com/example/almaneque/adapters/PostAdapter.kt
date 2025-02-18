package com.example.almaneque.adapters

import ImageCarouselAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.almaneque.databinding.ItemPostBinding
import com.example.almaneque.models.postdata

class PostAdapter(private val posts: MutableList<postdata>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: postdata) {
            binding.apply {
                titleText.text = post.titulo
                categoryText.text = post.categoria_nombre
                contentText.text = post.contenido


                val base64Images = post.archivos.map { it.archivo }

                // Configurar el carrusel de imágenes
                val adapter = ImageCarouselAdapter(base64Images)
                postImageCarousel.adapter = adapter
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
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
