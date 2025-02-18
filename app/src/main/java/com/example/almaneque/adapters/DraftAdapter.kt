import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.almaneque.databinding.ItemDraftBinding
import com.example.almaneque.models.Borrador
import com.example.almaneque.models.Draft

class DraftAdapter(
    private val drafts: MutableList<Borrador>,
    private val onEditClick: (Borrador) -> Unit,
    private val onDeleteClick: (Borrador) -> Unit
) : RecyclerView.Adapter<DraftAdapter.DraftViewHolder>() {

    inner class DraftViewHolder(private val binding: ItemDraftBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(draft: Borrador) {
            binding.apply {
                titleText.text = draft.titulo
                titleText.setOnClickListener { onEditClick(draft) }
                draftDelete.setOnClickListener { onDeleteClick(draft) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftViewHolder {
        val binding = ItemDraftBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DraftViewHolder(binding)
    }

    fun updateDrafts(newDrafts: List<Borrador>) {
        drafts.clear()
        drafts.addAll(newDrafts)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: DraftViewHolder, position: Int) {
        holder.bind(drafts[position])
    }

    override fun getItemCount() = drafts.size
}
