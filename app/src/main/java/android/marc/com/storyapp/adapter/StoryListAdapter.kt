package android.marc.com.storyapp.adapter

import android.marc.com.storyapp.databinding.RowStoryBinding
import android.marc.com.storyapp.model.Story
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class StoryListAdapter(private val storyList: List<Story>) : RecyclerView.Adapter<StoryListAdapter.StoryViewHolder>() {

    private var onItemClickCallBack: OnItemClickCallback? = null

    inner class StoryViewHolder(var binding: RowStoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = RowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = storyList[position]
        Glide.with(holder.itemView.context)
            .load(story.photoUrl)
            .into(holder.binding.imageStory)
        holder.apply {
            binding.apply {
                tvStoryName.text = story.name
                itemView.setOnClickListener {
                    onItemClickCallBack?.onItemClicked(story.id)
                }
            }
        }
    }

    override fun getItemCount(): Int = storyList.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallBack = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(storyId: String)
    }
}