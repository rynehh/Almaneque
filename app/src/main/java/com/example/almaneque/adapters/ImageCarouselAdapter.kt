import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.almaneque.R

class ImageCarouselAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ImageCarouselAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(imageData: String) {
            if (imageData.startsWith("R.drawable")) {
                // Es un recurso local
                val resourceId = itemView.context.resources.getIdentifier(
                    imageData.removePrefix("R.drawable."),
                    "drawable",
                    itemView.context.packageName
                )
                imageView.setImageResource(resourceId)
            } else {
                // Es una imagen en Base64
                val decodedBytes = Base64.decode(imageData, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                imageView.setImageBitmap(bitmap)
            }
        }
    }
}


