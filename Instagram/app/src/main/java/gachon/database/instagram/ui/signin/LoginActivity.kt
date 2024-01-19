package gachon.database.instagram.ui.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import gachon.database.instagram.R
import gachon.database.instagram.data.LoginUser
import gachon.database.instagram.databinding.ActivityLoginBinding
import gachon.database.instagram.ui.main.MainActivity
import gachon.database.instagram.ui.signup.SignupActivity
import gachon.database.instagram.ui.signup.SignupUserNameFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initClickListener()
    }

    override fun onStart() {
        super.onStart()

        setLogin()
    }

    private fun initClickListener() {
        /* 새 계정 만들기 버튼 클릭 */
        binding.loginMakeNewAccountBtn.setOnClickListener {
            // 회원 가입 화면으로 이동
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun setLogin() {
        binding.loginBtn.setOnClickListener {
            // 로그인 버튼 클릭 시 DB 정보 조회
            getDatabaseData()
        }
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

    private fun moveToMainActivity(user: LoginUser) {
        // 유저 정보를 spf에 저장
        saveLoginUserInfo(user)

        // 다음 화면으로 넘길 데이터
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        val bundle = Bundle().apply {
            val gson = Gson()
            val userJson = gson.toJson(user)
            putString("user", userJson)
        }
        intent.putExtras(bundle)
        Log.d("LoginActivity", "넘기기: $user")
        // 화면 이동
        startActivity(intent)
        finish()
    }

    private fun getDatabaseData() {
        GlobalScope.launch(Dispatchers.IO) {
            val connection = connectToDatabase()

            if (connection != null) {
                connection.use { connection ->
                    val user = getLoginUserInfo(connection)
                    Log.d("LoginActivity", user.toString())
                    // 유저 정보를 spf에 넣어줌
                    if (user != null) {
                        // 메인 액티비티로 이동
                        moveToMainActivity(user)
                    }
                }
            } else {
                Log.d("Database", "Failed to connect to the database.")
            }
        }
    }

    fun getLoginUserInfo(connection: Connection): LoginUser? {
        val userName = binding.loginUserEt.text.toString()
        val pwd = binding.loginPwdEt.text.toString()

        // 쿼리 작성
        val sql = String.format(resources.getString(R.string.query_select_login_user), userName, pwd)

        return try {
            // Statement 객체를 생성하여 SQL 쿼리를 실행하기 위한 준비를 함
            val statement = connection.createStatement()
            // SQL SELECT 쿼리를 실행하고, 조회 결과를 테이블 형식의 데이터인 ResultSet 객체에 저장함
            val resultSet = statement.executeQuery(sql)

            var user = LoginUser(0, "", "", 0, 0, "")
            while (resultSet.next()) { // 조회 결과를 한줄 한줄 받아옴
                val id = resultSet.getInt("user_id")
                val userName = resultSet.getString("user_name")
                val name = resultSet.getString("name")
                val followerNum = resultSet.getInt("follower_count")
                val followingNum = resultSet.getInt("following_count")
                val profileImage = resultSet.getString("profileImage_url")
                user = LoginUser(id, userName, name, followerNum, followingNum, profileImage)
            }

            user
        } catch (e: SQLException) {
            println("An error occurred while executing the SQL query: $sql")
            println(e.message)

            return null
        }
    }

    private fun saveLoginUserInfo(user: LoginUser) {
        // spf에 로그인한 유저 정보를 저장
        val spf = getSharedPreferences("user", MODE_PRIVATE)
        val editor = spf.edit()
        val gson = Gson()
        val userJson = gson.toJson(user) // 템플릿 데이터 변환

        editor
            .putInt("userId", user.id)
            .putString("user", userJson)
            .apply()
    }
}