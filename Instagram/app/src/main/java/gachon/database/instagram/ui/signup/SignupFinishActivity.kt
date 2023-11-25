package gachon.database.instagram.ui.signup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gachon.database.instagram.R
import gachon.database.instagram.databinding.ActivitySignupFinishBinding
import gachon.database.instagram.ui.MainActivity

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
            val nextIntent = Intent(this, MainActivity::class.java)
            userName = intent.getStringExtra("user_name").toString()
            password = intent.getStringExtra("password").toString()
            nextIntent.putExtra("user_name", userName)
            nextIntent.putExtra("password", password)
            // 화면 이동
            startActivity(nextIntent)
        }
    }

    private fun setInit() {
        binding.signupFinishWelcomeTv.text =
            getString(R.string.signup_finish_welcome_tv, intent.getStringExtra("user_name"))

        binding.signupFinishNoticeTv.text =
            getString(R.string.signup_finish_notice_tv, intent.getStringExtra("user_name"))
    }
}