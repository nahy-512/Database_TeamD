package gachon.database.instagram.ui.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import gachon.database.instagram.R
import gachon.database.instagram.databinding.ActivitySignupFinishBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import gachon.database.instagram.ui.signin.LoginActivity

class SignupFinishActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupFinishBinding

    private var userName = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupFinishBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initClickListener()
        setInit()
    }

    private fun initClickListener() {
        // 개인정보 설정
        binding.signupFinishNextBtn.setOnClickListener {
            val nextIntent = Intent(this, LoginActivity::class.java)
            userName = intent.getStringExtra("user_name").toString()
            password = intent.getStringExtra("password").toString()
            nextIntent.putExtra("user_name", userName)
            nextIntent.putExtra("password", password)
            insertDatabaseData()
            // 화면 이동
            Toast.makeText(this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()
            startActivity(nextIntent)
            // 스택에 쌓여있는 회원가입 액티비티들을 모두 종료
            finishAffinity()
        }
    }

    private fun setInit() {
        binding.signupFinishWelcomeTv.text =
            getString(R.string.signup_finish_welcome_tv, intent.getStringExtra("user_name"))

        binding.signupFinishNoticeTv.text =
            getString(R.string.signup_finish_notice_tv, intent.getStringExtra("user_name"))

        binding.signupFinishPolicyTv.text =
            getString(R.string.signup_finish_policy_tv, intent.getStringExtra("user_name"))
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


    private fun insertDatabaseData() {
        GlobalScope.launch(Dispatchers.IO) {

            val connection = connectToDatabase()

            if (connection != null) {
                connection.use { connection ->
                    // database에 회원가입 data insert
                    insertUserData(connection)
                }
            } else {
                Log.d("Database", "Failed to connect to the database.")
            }
        }
    }


    private fun insertUserData(connection: Connection) {

        val sql = getString(R.string.query_insert_signup_user, userName, password);

        try {
            val statement = connection.createStatement()
            val resultSet = statement.executeUpdate(sql)

            Log.d("SignupFinishActivity", "회원가입 성공")
        } catch (e: SQLException) {
            println("An error occurred while executing the SQL query: $sql")
            println(e.message)

            return
        }
    }
}