package gachon.database.instagram.ui.profile.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import gachon.database.instagram.data.User
import gachon.database.instagram.databinding.FragmentFollowListBinding
import gachon.database.instagram.ui.profile.follow.adapter.FollowRVAdapter

class FollowListFragment: Fragment() {

    lateinit var binding: FragmentFollowListBinding
    private lateinit var adapter: FollowRVAdapter

    private var isFollower: Boolean = true

    private var followDatas = ArrayList<User>()

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
        initFollowRv(isFollower)
    }

    private fun setInit() {
        binding.followListAllTv.text = if (isFollower) "모든 팔로워" else "정렬 기준 기본"
        binding.followListFollowingSortIv.visibility = if (isFollower) View.GONE else View.VISIBLE
    }

    private fun addFollowDummy() {
        //TODO: DB에서 조회한 팔로워, 팔로잉 정보 넣어주기
        if (isFollower) {
            followDatas.apply {
                add(User(2, "nah25_01", "pwd", "김나현", "email1"))
                add(User(1, "seol_212", "pwd", "설지은", "email2"))
            }
        } else {
            followDatas.apply {
                add(User(2, "nah25_01", "pwd", "김나현", "email1"))
                add(User(1, "seol_212", "pwd", "설지은", "email2"))
                add(User(3, "64.5h_", "pwd", "한희선", "email3"))
                add(User(4, "you_hi", "pwd", "유하연", "email4"))
            }
        }
    }

    private fun initFollowRv(isFollower: Boolean) {
        // 우선 임의로 더미데이터 삽입
        addFollowDummy()

        //TODO: 저장된 userId를 통해 팔로워, 팔로우 목록 불러오는 쿼리 날리기
        val userId: Int = getUserId()

        adapter = FollowRVAdapter(isFollower)
        binding.followListRv.adapter = adapter
        binding.followListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 쿼리문으로 조회한 팔로워 or 팔로잉 리스트 집어넣기
        adapter.addFollows(followDatas)

        //TODO: 아이템 버튼 클릭 시 팔로워 삭제 or 팔로잉 취소 구현
    }

    private fun getUserId(): Int {
        val spf = activity?.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("userId", 0)
    }
}