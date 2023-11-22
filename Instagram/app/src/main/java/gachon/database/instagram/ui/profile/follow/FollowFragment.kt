package gachon.database.instagram.ui.profile.follow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayoutMediator
import gachon.database.instagram.databinding.FragmentFollowBinding
import gachon.database.instagram.ui.MainActivity
import gachon.database.instagram.ui.profile.follow.adapter.FollowVPAdapter

class FollowFragment: Fragment() {

    lateinit var binding: FragmentFollowBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowBinding.inflate(inflater, container, false)

        initClickListener()
        setInit()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        setVPAdapter()
    }

    private fun initClickListener() {
        /* 뒤로가기 */
        binding.followBackIv.setOnClickListener {
            // 이전의 모든 프래그먼트를 백 스택에서 제거 (HomeFragment)
            (context as MainActivity).supportFragmentManager
                .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    private fun setInit() {
        Log.d("FollowFragment", "isFollower: ${arguments?.getBoolean("isFollower")}")
        // 프로필 화면에서 넘겨온 데이터를 받아옴
        binding.followUserNameTitleTv.text = arguments?.getString("user_name", "")
    }

    private fun setVPAdapter() {
        // 탭바 텍스트 뒤에 프로필 화면에서 받아온 팔로워, 팔로잉 수 추가
        val information = arrayListOf("팔로워 " + arguments?.getInt("followerNum"), "팔로잉 " + arguments?.getInt("followingNum"))

        // 뷰페이저 설정
        val albumAdapter = FollowVPAdapter(this)
        binding.followVp.adapter = albumAdapter

        // 탭 레이아웃을 뷰페이저와 동기화
        TabLayoutMediator(binding.followTb, binding.followVp) {
                tab, position ->
            tab.text = information[position]
        }.attach()

        // 현재 선택된 탭 설정
        val selectedTabPosition = if (arguments?.getBoolean("isFollower") == true) 0 else 1
        binding.followVp.setCurrentItem(selectedTabPosition, true)
        //TODO: 현재 선택된 탭 뷰페이저에도 반영
//        binding.followVp.currentItem = selectedTabPosition
        binding.followTb.selectTab(binding.followTb.getTabAt(selectedTabPosition))

    }
}
