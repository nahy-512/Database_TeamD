package gachon.database.instagram.ui.signup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gachon.database.instagram.R
import gachon.database.instagram.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // 액티비티 안의 첫 프래그먼트 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.signup_frm, SignupUserNameFragment())
            .commitAllowingStateLoss()
    }
}