package gachon.database.instagram.ui.profile.follow.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import gachon.database.instagram.ui.profile.follow.FollowListFragment

class FollowVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FollowListFragment.newInstance(true) // 팔로워
            else -> FollowListFragment.newInstance(false) // 팔로잉
        }
    }
}