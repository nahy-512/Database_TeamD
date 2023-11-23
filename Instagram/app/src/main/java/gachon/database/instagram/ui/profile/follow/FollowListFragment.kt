package gachon.database.instagram.ui.profile.follow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import gachon.database.instagram.R
import gachon.database.instagram.data.Follow
import gachon.database.instagram.databinding.FragmentFollowListBinding
import gachon.database.instagram.ui.MainActivity
import gachon.database.instagram.ui.profile.follow.adapter.FollowRVAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.SQLException

class FollowListFragment: Fragment() {

    lateinit var binding: FragmentFollowListBinding
    private lateinit var adapter: FollowRVAdapter

    /* isFollower이라면 팔로워 목록, 아니라면 팔로잉 목록 조회 */
    private var isFollower: Boolean = true
    private var follows = ArrayList<Follow>()

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

        // DB의 데이터 조회
        getDatabaseData()
        // 뷰 랜더링
        setInit()
    }

    private fun setInit() {
        binding.followListAllTv.text = if (isFollower) "모든 팔로워" else "정렬 기준 기본"
        binding.followListFollowingSortIv.visibility = if (isFollower) View.GONE else View.VISIBLE
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
                            follows.clear()
                            follows.addAll(users)

                            // 팔로워 or 팔로잉 리스트로 리사이클러뷰 데이터 초기화
                            initFollowRv()
                        }
                    }
                } else {
                    Log.d("Database", "Failed to connect to the database.")
                }
            }
        }
    }

    fun getAllFollowList(connection: Connection): List<Follow> {
        // 현재 탭이 어떤 탭인지에 따라 서브쿼리 안에 적어줄 변수를 구분해주기 (팔로워를 조회할지 팔로잉을 조회할지)
        val targetId = if (isFollower) "follower_id" else "following_id" // 조회 대상이 되는 유저의 id
        val table = if (isFollower) "follower" else "following" // 조회할 테이블
        val userId = getUserId() // 현재 로그인된 유저 아이디
        val sql = String.format(resources.getString(R.string.get_follow_list), targetId, table, userId)

        return try {
            // Statement 객체를 생성하여 SQL 쿼리를 실행하기 위한 준비를 함
            val statement = connection.createStatement()
            // SQL SELECT 쿼리를 실행하고, 조회 결과를 테이블 형식의 데이터인 ResultSet 객체에 저장함
            val resultSet = statement.executeQuery(sql)

            // 조회 결과를 반환할 리스트 (팔로워 또는 팔로잉 목록)
            val follows = ArrayList<Follow>()

            while (resultSet.next()) { // 조회 결과를 한줄 한줄 받아옴
                val id = resultSet.getInt("user_id")
                val userName = resultSet.getString("user_name")
                val name = resultSet.getString("name")
                // 한 유저(팔로워, 팔로잉) 인스턴스에 받아온 필드를 차례대로 넣어줌
                val user = Follow(id, userName, name)
                // 리스트에 위에서 받아온 유저 정보를 추가해줌
                follows.add(user)
            }
            // DB에서 조회한 팔로워, 팔로잉 리스트를 반환
            follows
        } catch (e: SQLException) {
            println("An error occurred while executing the SQL query: $sql")
            println(e.message)

            ArrayList()
        }
    }

    private fun initFollowRv() {
        // 리사이클러뷰 연결
        adapter = FollowRVAdapter(isFollower)
        binding.followListRv.adapter = adapter
        binding.followListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 쿼리문으로 조회한 팔로워 or 팔로잉 리스트를 리사이클러뷰에 집어넣기
        Log.d("FollowListFragment", "From database: $follows")
        adapter.addFollows(follows)

        //TODO: 아이템 버튼 클릭 시 팔로워 삭제 or 팔로잉 취소 구현
    }

    private fun getUserId(): Int {
        val spf = activity?.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("userId", 2)
    }
}