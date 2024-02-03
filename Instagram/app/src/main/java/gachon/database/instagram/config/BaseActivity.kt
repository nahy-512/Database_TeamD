package gachon.database.instagram.config

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import gachon.database.instagram.R
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

// 액티비티의 기본을 작성, 뷰 바인딩 활용
abstract class BaseActivity<B : ViewBinding>(private val inflate: (LayoutInflater) -> B) :
    AppCompatActivity() {
    protected lateinit var binding: B
        private set

    // 뷰 바인딩 객체를 받아서 inflate해서 화면을 만들어줌.
    // 즉 매번 onCreate에서 setContentView를 하지 않아도 됨.
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        // 화면을 세로로 고정
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
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

    // 토스트를 쉽게 띄울 수 있게 해줌.
    fun showCustomToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun checkLoginComplete(): Boolean {
        return getSharedPreferences("onBoarding", Context.MODE_PRIVATE).getBoolean("isLogin", false)
    }
}