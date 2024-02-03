package gachon.database.instagram.ui.signup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gachon.database.instagram.R
import gachon.database.instagram.config.BaseActivity
import gachon.database.instagram.databinding.ActivitySignupBinding

class SignupActivity : BaseActivity<ActivitySignupBinding>(ActivitySignupBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        // 액티비티 안의 첫 프래그먼트 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.signup_frm, SignupUserNameFragment())
            .commitAllowingStateLoss()
    }
}