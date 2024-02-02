package gachon.database.instagram.ui.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import gachon.database.instagram.R
import gachon.database.instagram.databinding.FragmentSignupFinishBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import gachon.database.instagram.ui.signin.LoginActivity

class SignupFinishFragment : Fragment() {

    lateinit var binding: FragmentSignupFinishBinding

    private var userName = ""
    private var password = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupFinishBinding.inflate(inflater, container, false)

        initClickListener()
        setInit()

        return binding.root
    }

    private fun initClickListener() {
        // 회원가입 완료 (Fragment -> Activity)
        binding.signupFinishBtn.setOnClickListener {
            insertDatabaseData()
            Toast.makeText(requireContext(), "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()
            // 스택에 쌓여있는 회원가입 화면을 모두 없애고 화면 이동
            Intent(requireActivity(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }.also {
                startActivity(it)
            }
            activity?.finishAffinity()
        }
    }

    private fun setInit() {
        userName = arguments?.getString("user_name").toString()
        Log.d("SignupFinishFrag", "userName: $userName")

        binding.signupFinishWelcomeTv.text = getString(R.string.signup_finish_welcome_tv, userName)
        binding.signupFinishNoticeTv.text = getString(R.string.signup_finish_notice_tv, userName)
        binding.signupFinishPolicyTv.text = getString(R.string.signup_finish_policy_tv, userName)
    }


    private fun connectToDatabase(): Connection? {
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