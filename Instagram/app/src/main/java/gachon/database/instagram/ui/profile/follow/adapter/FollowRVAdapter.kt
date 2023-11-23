package gachon.database.instagram.ui.profile.follow.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import gachon.database.instagram.data.Follow
import gachon.database.instagram.databinding.ItemFollowBinding

class FollowRVAdapter(val isFollower: Boolean) : RecyclerView.Adapter<FollowRVAdapter.ViewHolder>() {

    private var users = ArrayList<Follow>()

    fun addFollows(follows: ArrayList<Follow>) {
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
        //TODO: 클릭했을 떄 버튼 색 바꿔주기 + 팔로워 삭제 또는 팔로잉 취소 구현
    }

    override fun getItemCount(): Int = users.size

    inner class ViewHolder(val binding: ItemFollowBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(follower: Follow) {
            binding.itemFollowUserNameTv.text = follower.userName
            binding.itemFollowNameTv.text = follower.name

            // 팔로워냐 팔로잉이냐에 따라 버튼을 다르게 구분해줌
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