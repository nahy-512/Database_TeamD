package gachon.database.instagram.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gachon.database.instagram.R
import gachon.database.instagram.databinding.FragmentProfileBinding
import gachon.database.instagram.ui.MainActivity
import gachon.database.instagram.ui.profile.follow.FollowFragment

class ProfileFragment: Fragment() {

    lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // 클릭 이벤트 정의
        initClickListener()

        return binding.root
    }

    private fun initClickListener() {
        binding.profileEditBtn.setOnClickListener {
            // 프로필 편집 화면으로 이동
            moveToEditActivity()
        }

        binding.profileFollowerLl.setOnClickListener {
            // 팔로워 화면으로 이동
            moveToFollowFragment(true)
        }

        binding.profileFollowingLl.setOnClickListener {
            // 팔로잉 화면으로 이동
            moveToFollowFragment(false)
        }
    }

    private fun moveToEditActivity() {
        val intent = Intent(activity, ProfileEditActivity::class.java)

        // 데이터 넣기
        intent.apply {
            this.putExtra("user_name", binding.profileUserNameTv.text.toString())
            this.putExtra("name", binding.profileNameTv.text)
        }
        // 화면 이동
        startActivity(intent)
    }

    private fun moveToFollowFragment(isFollower: Boolean) {
        val userName = binding.profileUserNameTv.text.toString()
        val followerCnt = binding.profileFollowerNumTv.text.toString().toInt()
        val followingCnt = binding.profileFollowingNumTv.text.toString().toInt()

        // 화면을 이동하면서 데이터 전달
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .add(R.id.main_frm, FollowFragment().apply {
                arguments = Bundle().apply {
                    putString("user_name", userName) // 유저 이름
                    putBoolean("isFollower", isFollower) // 팔로워인지, 팔로잉인지 구분
                    putInt("followerNum", followerCnt) // 팔로워 수 전달
                    putInt("followingNum", followingCnt) // 팔로잉 수 전달
                }
            })
            .addToBackStack(null) // 백 스택에 트랜잭션을 추가
            .commitAllowingStateLoss()
    }

}