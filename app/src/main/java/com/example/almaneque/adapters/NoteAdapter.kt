package com.example.almaneque.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.RecyclerView
import com.example.almaneque.R
import com.example.almaneque.databinding.ItemNoteBinding
import com.example.almaneque.models.notedata

class NoteAdapter(
    private var noteList: List<notedata>,
    private val onNoteOptionSelected: (actionId: Int, note: notedata) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: notedata) {
            binding.apply {
                titleText.text = note.titulo
                wateringText.text = "Regado: ${note.regado}"
                lightText.text = "Luz: ${note.luz}"
                fertilizationText.text = "Fertilización: ${note.fertilizacion}"
                weatherText.text = "Clima: ${note.clima}"
                extraInfoText.text = "Descripción: ${note.descripcion}"


                optionsButton.setOnClickListener { showNoteMenu(it, note) }
            }
        }


        private fun showNoteMenu(view: View, note: notedata) {
            val wrapper = ContextThemeWrapper(view.context, R.style.CenteredPopupMenuText)
            val popupMenu = PopupMenu(wrapper, view)
            popupMenu.menuInflater.inflate(R.menu.note_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit_note -> onNoteOptionSelected(menuItem.itemId, note)
                    R.id.action_delete_note -> onNoteOptionSelected(menuItem.itemId, note)
                }
                true
            }
            popupMenu.show()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(noteList[position])
    }

    override fun getItemCount() = noteList.size


    fun updateNotes(newNotes: List<notedata>) {
        noteList = newNotes
        notifyDataSetChanged()
    }
}
