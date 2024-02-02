package gachon.database.instagram.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import gachon.database.instagram.R
import gachon.database.instagram.data.User
import gachon.database.instagram.databinding.ActivityMainBinding
import gachon.database.instagram.ui.main.home.HomeFragment
import gachon.database.instagram.ui.main.profile.ProfileFragment
import gachon.database.instagram.ui.main.reels.ReelsFragment
import gachon.database.instagram.ui.main.search.SearchFragment
import gachon.database.instagram.ui.main.shop.ShopFragment
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 가장 처음에 표시할 Fragment 설정 -> ProfileFragment
        binding.mainBottomNavi.selectedItemId = R.id.profile
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, passLoginData(ProfileFragment()))
            .commitAllowingStateLoss()
        // 바텀네비 아이템 클릭 이벤트 정의
        setBottomNavi()

        // DB 데이터 조회 테스트
//        dbTest2()
    }

    private fun setBottomNavi() {
        // 각 아이템을 클릭했을 때 나타나는 이벤트 정의
        binding.mainBottomNavi.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    setFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.search -> {
                    setFragment(SearchFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.reels -> {
                    setFragment(ReelsFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.shop -> {
                    setFragment(ShopFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                    setFragment(ProfileFragment())
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun passLoginData(fragment: Fragment): Fragment {

        // 데이터 받기
        val data = intent.getStringExtra("user")

        Log.d("MainActivity", "받기: $data")

        // 프래그먼트에 데이터 전달
        val bundle = Bundle()
        bundle.putString("user", data)
        fragment.arguments = bundle

        return fragment
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_frm, fragment).commit()
    }

    private fun dbTest2() {
        val thread = Thread{
            var con: Connection? = null

            try {
                Class.forName("com.mysql.jdbc.Driver")
                val url = resources.getString(R.string.db_url)
                val user = resources.getString(R.string.db_user)
                val passwd = resources.getString(R.string.db_password)
                con = DriverManager.getConnection(url, user, passwd)
                Log.d("Database", con.toString())
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            // database operation
            var stmt: Statement? = null
            var rs: ResultSet? = null
            try {
                if (con != null) {
                    stmt = con.createStatement()
                    val sql = "select * from user"
                    rs = stmt.executeQuery(sql)
                    while (rs.next()) {
                        var name = rs.getString(1)
                        if (rs.wasNull()) name = "null"
                        var course_id = rs.getString(2)
                        if (rs.wasNull()) course_id = "null"
                        println(name + "\t" + course_id)
                    }
                }
            } catch (e1: SQLException) {
                e1.printStackTrace()
            }
            try {
                if (stmt != null && !stmt.isClosed()) stmt.close()
            } catch (e1: SQLException) {
                e1.printStackTrace()
            }
        }

        thread.start()
        try {
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun dbTest() {
        val thread = Thread{
            val connection = connectToDatabase()

            if (connection != null) {
                try {
                    val users = getAllUsers(connection)
                    for (user in users) {
                        Log.d("Database", user.toString())
                    }
                } finally {
                    // 연결 해제
                    connection.close()
                }
            } else {
                Log.d("Database", "Failed to connect to the database.")
            }
        }
        thread.start()
    }

    fun connectToDatabase(): Connection? {
        val url = resources.getString(R.string.db_url)
        val user = resources.getString(R.string.db_user)
        val password = resources.getString(R.string.db_password)

        return try {
            DriverManager.getConnection(url, user, password)
        } catch (e: SQLException) {
            null
        }
    }

    fun getAllUsers(connection: Connection): List<User> {
        val sql = "SELECT * FROM user"

        return try {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(sql)

            val users = ArrayList<User>()

            while (resultSet.next()) {
                val id = resultSet.getInt("user_id")
                val userName = resultSet.getString("user_name")
                val password = resultSet.getString("password")
                val name = resultSet.getString("name")
                val email = resultSet.getString("email")
                val introduction = resultSet.getString("introduction")
                val profileImage = resultSet.getString("profileImage_url")

                val user = User(id, userName, password, name, email, profileImage, introduction)
                users.add(user)
            }

            users
        } catch (e: SQLException) {
            println("An error occurred while executing the SQL query: $sql")
            println(e.message)

            ArrayList()
        }
    }
}