package gachon.database.instagram.ui.main.profile.follow.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import gachon.database.instagram.data.Follow
import gachon.database.instagram.databinding.ItemRecommendFollowBinding

class RecommendFollowRVAdapter : RecyclerView.Adapter<RecommendFollowRVAdapter.ViewHolder>() {

    private var users = ArrayList<Follow>()

    fun addRecommendList(follows: ArrayList<Follow>) {
        this.users.clear()
        this.users.addAll(follows)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemRecommendFollowBinding = ItemRecommendFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class ViewHolder(val binding: ItemRecommendFollowBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(follower: Follow) {
            binding.itemFollowRecommendUserNameTv.text = follower.userName
//            binding.itemFollowRecommendDescTv.text = follower.name
        }
    }
}