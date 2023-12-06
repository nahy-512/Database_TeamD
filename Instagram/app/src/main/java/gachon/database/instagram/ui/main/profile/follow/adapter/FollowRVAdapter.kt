package gachon.database.instagram.ui.main.profile.follow.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import gachon.database.instagram.R
import gachon.database.instagram.data.Follow
import gachon.database.instagram.databinding.ItemFollowBinding

class FollowRVAdapter(val context: Context, val isFollower: Boolean) : RecyclerView.Adapter<FollowRVAdapter.ViewHolder>() {

    interface MyItemClickListener {
        //TODO: 일단 다 취소하는 경우
        fun onClickBtn(follow: Follow, isCancle: Boolean) // 버튼 상태 변경 및 targetId 반환
//        fun onRemoveSong(followerId: Int) // 찐 아이템 삭제 진행 (팔로워 삭제의 경우)
    }

    private var users = ArrayList<Follow>()

    // 팔로잉, 팔로워 상태를 저장하는 변수
    private lateinit var followStatusList: MutableList<Boolean>

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    fun addFollows(follows: ArrayList<Follow>) {
        this.users.clear()
        this.users.addAll(follows)
        // 기본은 팔로잉한 상태
        this.followStatusList = MutableList(users.size) { true }

        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        users.removeAt(position)
        notifyDataSetChanged()
//        notifyItemRemoved(position)
//        notifyItemRangeRemoved(position, songList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemFollowBinding = ItemFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
        Log.d("bori", "$position + ${users[position]}")
        //TODO: 클릭했을 때 버튼 색 바꿔주기 + 팔로워 삭제 또는 팔로잉 취소 구현

        // 팔로워
        holder.apply {
            if (isFollower) { /* 팔로워: (회색) 삭제 -> 아이템이 바로 사라짐 */
                binding.itemFollowFollowerBtn.setOnClickListener {
                    // 일단 아이템 클릭 -> 팔로워 정보 반환
                    mItemClickListener.onClickBtn(users[position], true)
                    // 팔로워의 삭제는 클릭하면 바로 삭제 (+ '삭제됨' 토스트 메시지)
                    removeItem(position)
                }
            } else {  /* 팔로잉: (회색) 팔로잉 -> (파란색) 팔로잉 -> (회색) 팔로잉 */
                // 버튼 상태 구분 필요
                binding.itemFollowFollowingBtn.setOnClickListener {
                    // 팔로워 상태 설정 (기본: true)
                    val isFollowing = followStatusList[position]
                    // 클릭한 아이템 상태 변경
                    followStatusList[position] = !isFollowing
                    // 버튼 색 바꿔주기
                    changeFollowingBtn(binding.itemFollowFollowingBtn, isFollowing)
                    // 일단 아이템 클릭 -> 팔로잉 정보 반환
                    mItemClickListener.onClickBtn(users[position], isFollowing)
                }
            }
        }
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

    fun changeFollowingBtn(button: TextView, isFollowing: Boolean) {
        /* 팔로잉: (회색) 팔로잉 -> (파란색) 팔로우 -> (회색) 팔로잉 */
        if (isFollowing) { // 다시 팔로잉하는 경우
            Log.d("FollowRVAdapter", "재팔로잉 / 파란색, 팔로우")
            // 파랗게 색칠
            button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.default_blue))
            // 텍스트 변경
            button.text = "팔로우"
            button.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else { // 팔로잉 취소하는 경우
            Log.d("FollowRVAdapter", "팔로잉 취소 / 회색, 팔로잉")
            // 회색으로 색칠
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F0F0F0"))
            // 텍스트 변경
            button.text = "팔로잉"
            button.setTextColor(ContextCompat.getColor(context, R.color.dark_gray))
        }
    }
}