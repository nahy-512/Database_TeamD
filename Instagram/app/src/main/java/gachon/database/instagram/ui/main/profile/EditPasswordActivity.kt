package gachon.database.instagram.ui.main.profile

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import gachon.database.instagram.R
import gachon.database.instagram.databinding.ActivityEditPasswordBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class EditPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPasswordBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initClickListener()
        setInit()
    }

    private fun initClickListener() {
        // 뒤로가기 버튼 클릭
        binding.editPasswordBackIv.setOnClickListener {
            finish()
        }

        // 비밀번호 변경
        binding.editPasswordSaveBtn.setOnClickListener {
            Log.d("EditPasswordAct", "비밀번호 변경 버튼 클릭")
            // DB 접근
            updateDatabaseData()
//            if (checkPwdValidation()) { // 유효성 검사
//                // 비밀번호 업데이트
//                updateDatabaseData()
//            }
        }
    }

    private fun setInit() {
        binding.editPasswordUserNameTv.text =
            getString(R.string.edit_password_user_name_tv, intent.getStringExtra("user_name"))
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

    private fun checkPwdValidation(): Boolean {
        //TODO: 유효성 검사 로직 보충
        if (binding.editPasswordNewPwdEt.text != binding.editPasswordNewPwdRepeatEt.text) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            Log.d(
                "EditPasswordAct",
                "새 비밀번호: ${binding.editPasswordNewPwdEt.text}\n 비밀번호 확인: ${binding.editPasswordNewPwdRepeatEt.text}"
            )
            return false
        }
        return true
    }

    private fun updateDatabaseData() {
        GlobalScope.launch(Dispatchers.IO) {

            val connection = connectToDatabase()

            if (connection != null) {
                connection.use { connection ->
                    // 비밀번호 업데이트
                    updateUserPassword(connection)
                }
            } else {
                Log.d("Database", "Failed to connect to the database.")
            }
        }
    }

    private fun updateUserPassword(connection: Connection) {
        val currentPwd = binding.editPasswordCurrentPwdEt.text
        val newPwd = binding.editPasswordNewPwdEt.text

        val sql = getString(R.string.query_update_password, newPwd, currentPwd, getUserId())

        try {
            val statement = connection.createStatement()
            val resultSet = statement.executeUpdate(sql)

            //TODO: 비밀번호 업데이트가 제대로 되지 않았다면 (기존 비밀번호와 다르다면) 오류 메시지 띄워주기

            Log.d("EditPasswordActivity", "비밀번호 업데이트 성공")
            // 종료
            finish()
        } catch (e: SQLException) {
            println("An error occurred while executing the SQL query: $sql")
            println(e.message)

            return
        }
    }

    private fun getUserId(): Int {
        val spf = getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("userId", 2)
    }
}