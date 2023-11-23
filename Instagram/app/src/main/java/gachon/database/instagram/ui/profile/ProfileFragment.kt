package gachon.database.instagram.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import gachon.database.instagram.R
import gachon.database.instagram.data.Follow
import gachon.database.instagram.databinding.FragmentProfileBinding
import gachon.database.instagram.ui.MainActivity
import gachon.database.instagram.ui.profile.follow.FollowFragment
import gachon.database.instagram.ui.profile.follow.adapter.RecommendFollowRVAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.SQLException

class ProfileFragment: Fragment() {

    lateinit var binding: FragmentProfileBinding
    private lateinit var adapter: RecommendFollowRVAdapter

    private var isShowRecommend = false
    private var recommends = ArrayList<Follow>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // 클릭 이벤트 정의
        initClickListener()
        // 초기 뷰 세팅
        setInit()

        return binding.root
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
            //TODO: 이미지 변경
            if (isShowRecommend) { // 펼쳐진 상태
                binding.profileRecommendFollowCl.visibility = View.GONE
            } else { // 닫혀있는 상태
                binding.profileRecommendFollowCl.visibility = View.VISIBLE
            }
            // 접힘 상태 업데이트
            isShowRecommend = !isShowRecommend
        }
    }

    private fun setInit() {
        binding.profileRecommendFollowCl.visibility = if (isShowRecommend) View.VISIBLE else View.GONE
    }

    private fun getDatabaseData() {
        GlobalScope.launch(Dispatchers.IO) {
            if (activity is MainActivity) {
                val activity = activity as MainActivity
                val connection = activity.connectToDatabase()

                if (connection != null) {
                    connection.use { connection ->
                        // DB에서 조회한 팔로워 or 팔로잉 정보를 리스트로 가져오기
                        val users = getAllFollowList(connection)

                        // UI 업데이트를 메인 스레드에서 수행
                        withContext(Dispatchers.Main) {
                            recommends.clear()
                            recommends.addAll(users)

                            // 팔로워 or 팔로잉 리스트로 리사이클러뷰 데이터 초기화
                            initRecommendRV()
                        }
                    }
                } else {
                    Log.d("Database", "Failed to connect to the database.")
                }
            }
        }
    }

    fun getAllFollowList(connection: Connection): List<Follow> {
        //TODO: 내가 팔로잉하지 않는 유저를 추천받아서 조회
        val query = "SELECT * FROM user"

        return try {
            // Statement 객체를 생성하여 SQL 쿼리를 실행하기 위한 준비를 함
            val statement = connection.createStatement()
            // SQL SELECT 쿼리를 실행하고, 조회 결과를 테이블 형식의 데이터인 ResultSet 객체에 저장함
            val resultSet = statement.executeQuery(query)

            // 조회 결과를 반환할 리스트 (추천 유저 목록)
            val recommends = ArrayList<Follow>()

            while (resultSet.next()) { // 조회 결과를 한줄 한줄 받아옴
                val id = resultSet.getInt("user_id")
                val userName = resultSet.getString("user_name")
                val name = resultSet.getString("name")
                // 한 유저 인스턴스에 받아온 필드를 차례대로 넣어줌
                val user = Follow(id, userName, name)
                // 리스트에 위에서 받아온 유저 정보를 추가해줌
                recommends.add(user)
            }
            // DB에서 조회한 추천 유저 리스트를 반환
            recommends
        } catch (e: SQLException) {
            println("An error occurred while executing the SQL query: $query")
            println(e.message)

            ArrayList()
        }
    }

    private fun initRecommendRV() {
        // 리사이클러뷰 연결
        adapter = RecommendFollowRVAdapter()
        binding.profileRecommendFollowRv.adapter = adapter
        binding.profileRecommendFollowRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // 쿼리문으로 조회한 팔로워 or 팔로잉 리스트를 리사이클러뷰에 집어넣기
        Log.d("ProfileFragment", "From database: $recommends")
        adapter.addRecommendList(recommends)
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