package gachon.database.instagram.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import gachon.database.instagram.databinding.ActivitySignupPwBinding

class SignupPWActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupPwBinding

    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupPwBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initClickListener()
    }

    private fun initClickListener() {
        // 개인정보 설정
        binding.signupPwNextBtn.setOnClickListener {
            val nextIntent = Intent(this, SignupFinishActivity::class.java)
            password = binding.signupPwEt.text.toString()
            nextIntent.putExtra("user_name", intent.getStringExtra("user_name"))
            nextIntent.putExtra("password", password)
            // 화면 이동
            startActivity(nextIntent)
        }
    }
}