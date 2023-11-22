package gachon.database.instagram.ui.profile.follow.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import gachon.database.instagram.data.User
import gachon.database.instagram.databinding.ItemFollowBinding

class FollowRVAdapter(val isFollower: Boolean) : RecyclerView.Adapter<FollowRVAdapter.ViewHolder>() {

    private var users = ArrayList<User>()

    fun addFollows(follows: ArrayList<User>) {
        this.users.clear()
        this.users.addAll(follows)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowRVAdapter.ViewHolder {
        val binding: ItemFollowBinding = ItemFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowRVAdapter.ViewHolder, position: Int) {
        holder.bind(users[position])
        Log.d("bori", "$position + ${users[position]}")
    }

    override fun getItemCount(): Int = users.size

    inner class ViewHolder(val binding: ItemFollowBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(follower: User) {
            binding.itemFollowUserNameTv.text = follower.userName
            binding.itemFollowNameTv.text = follower.name

            if (isFollower) {
                binding.itemFollowFollowerBtn.visibility = View.VISIBLE
                binding.itemFollowFollowingBtn.visibility = View.GONE
            } else {
                binding.itemFollowFollowerBtn.visibility = View.GONE
                binding.itemFollowFollowingBtn.visibility = View.VISIBLE
            }
        }
    }
}