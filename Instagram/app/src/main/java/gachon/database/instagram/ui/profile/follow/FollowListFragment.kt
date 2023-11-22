package gachon.database.instagram.ui.profile.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gachon.database.instagram.databinding.FragmentFollowListBinding

class FollowListFragment: Fragment() {

    lateinit var binding: FragmentFollowListBinding

    private var isFollower: Boolean = true

    companion object {
        fun newInstance(isFollower: Boolean): FollowListFragment {
            val fragment = FollowListFragment()
            fragment.isFollower = isFollower
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFollowListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        setInit()
    }

    private fun setInit() {
        binding.followTv.text = if (isFollower) "팔로워" else "팔로잉"
    }
}