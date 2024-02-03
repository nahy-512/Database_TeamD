package gachon.database.instagram.ui.main.profile.follow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import gachon.database.instagram.R
import gachon.database.instagram.config.BaseFragment
import gachon.database.instagram.data.Follow
import gachon.database.instagram.databinding.FragmentFollowListBinding
import gachon.database.instagram.ui.main.MainActivity
import gachon.database.instagram.ui.main.profile.follow.adapter.FollowRVAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.SQLException

class FollowListFragment: BaseFragment<FragmentFollowListBinding>(FragmentFollowListBinding::bind, R.layout.fragment_follow_list) {

    private lateinit var adapter: FollowRVAdapter

    /* isFollower이라면 팔로워 목록, 아니라면 팔로잉 목록 조회 */
    private var isFollower: Boolean = true
    private var follows = ArrayList<Follow>()

    // 현재 로그인 된 유저 아이디
    private var userId: Int = 2

    companion object {
        fun newInstance(isFollower: Boolean): FollowListFragment {
            val fragment = FollowListFragment()
            fragment.isFollower = isFollower
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        // 현재 로그인된 user_id 초기화
        userId = getUserId()
    }

    private fun getDatabaseData() {
        GlobalScope.launch(Dispatchers.IO) {
            val connection = connectToDatabase()
            if (connection != null) {
                // DB에서 조회한 팔로워 or 팔로잉 정보를 리스트로 가져오기
                val users = getAllFollowList(connection)

                // UI 업데이트를 메인 스레드에서 수행
                withContext(Dispatchers.Main) {
                    follows.clear()
                    follows.addAll(users)
                    // 팔로워 or 팔로잉 리스트로 리사이클러뷰 데이터 초기화
                    initFollowRv()
                    // 연결 닫기
                    connection.close()
                }
            } else {
                Log.d("Database", "Failed to connect to the database.")
            }
        }
    }

    fun getAllFollowList(connection: Connection): List<Follow> {
        // 현재 탭이 어떤 탭인지에 따라 서브쿼리 안에 적어줄 변수를 구분해주기 (팔로워를 조회할지 팔로잉을 조회할지)
        val targetId = if (isFollower) "follower_id" else "following_id" // 조회 대상이 되는 유저의 id
        val table = if (isFollower) "follower" else "following" // 조회할 테이블
        val sql = String.format(resources.getString(R.string.query_select_follow_list), targetId, table, userId)

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
                val profileImage = resultSet.getString("profileImage_url")
                // 한 유저(팔로워, 팔로잉) 인스턴스에 받아온 필드를 차례대로 넣어줌
                val user = Follow(id, userName, name, profileImage)
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

    private fun onClickFollowDeleteBtn(follow: Follow, isFollowing: Boolean, isCancle: Boolean) {
        GlobalScope.launch(Dispatchers.IO) {
            val connection = connectToDatabase()
            if (connection != null) {
                if (isFollowing) { // 팔로워 삭제
                    deleteFollower(connection, follow.userId) { success ->
                        if (success) {
                            //TODO: 삭제 성공 시 토스트 메시지 표시

                            // 일단 로그 출력
                            Log.d("FollowListFrag", "팔로워 삭제 성공, user_name: ${follow.userName}")
                        }
                        // 연결 닫기
                        connection.close()
                    }
                } else { // 팔로잉
                    if (isCancle) { // 취소의 경우
                        cancelFollowing(connection, follow.userId) { success ->
                            if (success) {
                                Log.d("FollowListFrag", "팔로잉 취소 성공, user_name: ${follow.userName}")
                            }
                            // 연결 닫기
                            connection.close()
                        }
                    } else { // 팔로우를 다시 하는 경우 (재팔로우)
                        setFollowing(connection, follow.userId) { success ->
                            if (success) {
                                Log.d("FollowListFrag", "재팔로우 성공, user_name: ${follow.userName}")
                            }
                            // 연결 닫기
                            connection.close()
                        }
                    }
                }
            } else {
                Log.d("Database", "Failed to connect to the database.")
            }
        }
    }

    private fun deleteFollower(connection: Connection, followerId: Int, onSuccess: (Boolean) -> Unit) {
        // 나를 팔로워하는 사람 삭제 (나의 팔로워)
        val sql_my = String.format(resources.getString(R.string.query_delete_follower_my), userId, followerId)
        // 그 사람의 팔로잉에서 나를 삭제
        val sql_other = String.format(resources.getString(R.string.query_delete_follower_other), userId, followerId)

        try {
            val statement = connection.createStatement()

            // 첫 번째 DELETE 쿼리 실행
            val rowsAffectedMy = statement.executeUpdate(sql_my)
            // 두 번째 DELETE 쿼리 실행
            val rowsAffectedOther = statement.executeUpdate(sql_other)

            // 양쪽 모두에서 영향을 받은 행(row)이 존재하면 성공으로 간주
            onSuccess(rowsAffectedMy > 0 && rowsAffectedOther > 0)
        } catch (e: SQLException) {
            println("An error occurred while executing the SQL query: \n$sql_my\n$sql_other")
            println(e.message)
            // 삭제 실패
            onSuccess(false)
        }
    }

    private fun cancelFollowing(connection: Connection, followingId: Int, onSuccess: (Boolean) -> Unit) {
        // 내가 팔로잉을 취소
        val sql_my = String.format(resources.getString(R.string.query_delete_following_my), userId, followingId)
        // 그 사람의 팔로워에서 나를 삭제
        val sql_other = String.format(resources.getString(R.string.query_delete_following_other), userId, followingId)

        try {
            val statement = connection.createStatement()

            // 첫 번째 DELETE 쿼리 실행
            val rowsAffectedMy = statement.executeUpdate(sql_my)
            // 두 번째 DELETE 쿼리 실행
            val rowsAffectedOther = statement.executeUpdate(sql_other)

            // 양쪽 모두에서 영향을 받은 행(row)이 존재하면 성공으로 간주
            onSuccess(rowsAffectedMy > 0 && rowsAffectedOther > 0)
        } catch (e: SQLException) {
            println("An error occurred while executing the SQL query: \n$sql_my\n$sql_other")
            println(e.message)
            onSuccess(false)
        }
    }

    private fun setFollowing(connection: Connection, followingId: Int, onSuccess: (Boolean) -> Unit) {
        // 내가 팔로우
        val sql_my = String.format(resources.getString(R.string.query_insert_following_my), userId, followingId)
        // 그 사람의 팔로워에 나를 추가
        val sql_other = String.format(resources.getString(R.string.query_insert_following_other), userId, followingId)

        try {
            val statement = connection.createStatement()

            // 첫 번째 DELETE 쿼리 실행
            val rowsAffectedMy = statement.executeUpdate(sql_my)
            // 두 번째 DELETE 쿼리 실행
            val rowsAffectedOther = statement.executeUpdate(sql_other)

            // 양쪽 모두에서 영향을 받은 행(row)이 존재하면 성공으로 간주
            onSuccess(rowsAffectedMy > 0 && rowsAffectedOther > 0)
        } catch (e: SQLException) {
            println("An error occurred while executing the SQL query: \n$sql_my\n$sql_other")
            println(e.message)
            onSuccess(false)
        }
    }

    private fun initFollowRv() {
        // 리사이클러뷰 연결
        adapter = FollowRVAdapter(requireContext(), isFollower)
        binding.followListRv.adapter = adapter
        binding.followListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 쿼리문으로 조회한 팔로워 or 팔로잉 리스트를 리사이클러뷰에 집어넣기
        Log.d("FollowListFragment", "From database: $follows")
        adapter.addFollows(follows)

        // 아이템 버튼 클릭 동작 -> 팔로워 삭제 or 팔로잉 취소
        adapter.setMyItemClickListener(object : FollowRVAdapter.MyItemClickListener {
            override fun onClickBtn(follow: Follow, isCancle: Boolean) {
                val followString = if (isFollower) "팔로워" else "팔로잉"
                //TODO: 팔로잉, 팔로우 취소 DB delete 구현
//                Toast.makeText(requireContext(), "$followString 탭 - ${follow.userName}", Toast.LENGTH_SHORT).show()
                onClickFollowDeleteBtn(follow, isFollower, isCancle)
            }
        })
    }

    private fun getUserId(): Int {
        val spf = activity?.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("userId", 2)
    }
}