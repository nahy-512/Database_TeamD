package gachon.database.instagram.ui.signup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gachon.database.instagram.R
import gachon.database.instagram.databinding.ActivitySignupUserNameBinding
import gachon.database.instagram.ui.profile.EditPasswordActivity

class SignupUserNameActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupUserNameBinding

    private var userName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupUserNameBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initClickListener()
    }

    private fun initClickListener() {
        // 개인정보 설정
        binding.signupUserNameNextBtn.setOnClickListener {
            val nextIntent = Intent(this, SignupPWActivity::class.java)
            userName = binding.signupUserNameEt.text.toString()
            nextIntent.putExtra("user_name", userName)
            // 화면 이동
            startActivity(nextIntent)
        }
    }
}