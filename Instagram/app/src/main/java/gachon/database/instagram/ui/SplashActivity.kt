package gachon.database.instagram.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import gachon.database.instagram.databinding.ActivitySplashBinding
import gachon.database.instagram.ui.main.MainActivity
import gachon.database.instagram.ui.signin.LoginActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val handler = Handler(Looper.getMainLooper())
        
        handler.postDelayed({
            // 자동로그인
            autoLogin()
        }, 1000)
    }

    private fun autoLogin() {
        if (getJwt() != 0) { // 유저 로그인 정보가 있으면 메인 액티비티로
            moveToNextActivity(MainActivity::class.java)
        } else { // 없다면 로그인 액티비티로
            moveToNextActivity(LoginActivity::class.java)
        }
        // 스플래시는 종료
        finish()
    }

    private fun moveToNextActivity(nextActivity: Class<*>) {
        startActivity(Intent(this, nextActivity))
    }

    private fun getJwt(): Int {
        val spf = getSharedPreferences("user", MODE_PRIVATE)

        return spf!!.getInt("userId", 0)
    }
}