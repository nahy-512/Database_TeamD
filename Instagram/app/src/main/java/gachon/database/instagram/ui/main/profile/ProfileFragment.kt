package gachon.database.instagram.ui.main.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import gachon.database.instagram.R
import gachon.database.instagram.config.BaseFragment
import gachon.database.instagram.data.Follow
import gachon.database.instagram.data.LoginUser
import gachon.database.instagram.databinding.FragmentProfileBinding
import gachon.database.instagram.ui.main.MainActivity
import gachon.database.instagram.ui.main.profile.follow.FollowFragment
import gachon.database.instagram.ui.main.profile.follow.adapter.RecommendFollowRVAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ProfileFragment: BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::bind, R.layout.fragment_profile) {

    private lateinit var adapter: RecommendFollowRVAdapter

    private var userId: Int = 0
    private lateinit var userInfo: LoginUser

    private var isShowRecommend = false
    private var recommends = ArrayList<Follow>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 클릭 이벤트 정의
        initClickListener()
        // 초기 뷰 세팅
        setInit()
    }

    override fun onStart() {
        super.onStart()

        getDatabaseData()
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

        binding.profileRecommendFollowIv.setOnClickListener {
            if (isShowRecommend) { // 펼쳐진 상태
                binding.profileRecommendFollowCl.visibility = View.GONE
                binding.profileRecommendFollowIv.setImageResource(R.drawable.ic_recommend_friend_unselected)
            } else { // 닫혀있는 상태
                binding.profileRecommendFollowCl.visibility = View.VISIBLE
                binding.profileRecommendFollowIv.setImageResource(R.drawable.ic_recommend_friend_selected)
            }
            // 접힘 상태 업데이트
            isShowRecommend = !isShowRecommend
        }
    }

    private fun setInit() {
        // 추천 친구 접기 & 펼치기
        binding.profileRecommendFollowCl.visibility = if (isShowRecommend) View.VISIBLE else View.GONE

        userId = requireActivity().getSharedPreferences("user", Activity.MODE_PRIVATE).getInt("userId", 2)

        val gson = Gson()
        // argument에서 데이터를 꺼내기
        val userJson = arguments?.getString("user")

        // 로그인한 유저 데이터 받기 (MainActivity에서 받아온 정보)
        if (userJson != null) {
            val user = gson.fromJson(userJson, LoginUser::class.java)
            userId = user.id
            // 뷰에 랜더링
            initUserInfo(user)
        }
    }

    private fun initUserInfo(user: LoginUser) {
        Log.d("ProfileFragment", "받기: $user")
        with(binding) {
            profileUserNameTv.text = user.userName
            profileNameTv.text = user.name
            profileFollowerNumTv.text = user.followerCnt.toString()
            profileFollowingNumTv.text = user.followingCnt.toString()
            Glide.with(requireContext()).load(user.profileImage).error(R.drawable.ic_profile_default).into(profileIv)
        }
    }

    private fun getDatabaseData() {
        GlobalScope.launch(Dispatchers.IO) {

            connectToDatabase()?.use { connection ->
                // userId를 통해 조회한 유저 정보를 가져옴
                userInfo = getUserInfoById(connection)

                // UI 업데이트를 메인 스레드에서 수행
                withContext(Dispatchers.Main) {
                    // 가져온 정보로 뷰를 업데이트
                    initUserInfo(userInfo)
                }
            }
        }
    }

    fun getUserInfoById(connection: Connection): LoginUser {

        // 쿼리 작성
        val sql = String.format(resources.getString(R.string.query_select_user_by_userId), userId)

        return try {
            // Statement 객체를 생성하여 SQL 쿼리를 실행하기 위한 준비를 함
            val statement = connection.createStatement()
            // SQL SELECT 쿼리를 실행하고, 조회 결과를 테이블 형식의 데이터인 ResultSet 객체에 저장함
            val resultSet = statement.executeQuery(sql)

            var user = LoginUser(userId, "", "", 0, 0, "")
            while (resultSet.next()) { // 조회 결과를 한줄 한줄 받아옴
                val userName = resultSet.getString("user_name")
                val name = resultSet.getString("name")
                val followingNum = resultSet.getInt("following_count")
                val followerNum = resultSet.getInt("follower_count")
                val profileImage = resultSet.getString("profileImage_url")
                user = LoginUser(userId, userName, name, followerNum, followingNum, profileImage)
                Log.d("ProfileFrag", "로그인 한 유저 정보: $user")
            }

            user
        } catch (e: SQLException) {
            println("An error occurred while executing the SQL query: $sql")
            println(e.message)

            return userInfo
        }
    }

    // 프로필 -> 프로필 편집 화면으로 이동
    private fun moveToEditActivity() {
        val intent = Intent(activity, ProfileEditActivity::class.java)

        val gson = Gson()
        val userJson = gson.toJson(userInfo)
        intent.putExtra("user", userJson)

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