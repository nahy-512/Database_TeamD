package gachon.database.instagram.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import gachon.database.instagram.databinding.ActivityProfileEditBinding
import gachon.database.instagram.ui.signin.LoginActivity
import kotlin.math.log

class ProfileEditActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileEditBinding

    private var userName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initClickListener()
        setInit()
    }

    private fun initClickListener() {
        // 닫기 버튼 클릭
        binding.profileEditCloseIv.setOnClickListener {
            finish() // 뒤로가기
        }

        // 저장 버튼 클릭
        binding.profileEditSaveIv.setOnClickListener {
            //TODO: 프로필 편집 장태 저장
            finish()
        }

        // 개인정보 설정
        binding.profileEditSetPersonalInformationTv.setOnClickListener {
            val intent = Intent(this, EditPasswordActivity::class.java)
            intent.putExtra("user_name", userName)
            // 화면 이동
            startActivity(intent)
        }

        // 로그아웃
        binding.profileEditLogoutTv.setOnClickListener {
            logout()
        }
    }

    private fun setInit() {
        userName = intent.getStringExtra("user_name").toString()
        // 값 넣어주기
        with(binding) {
            profileEditUserNameEt.setText(userName)
            profileEditNameEt.setText(intent.getStringExtra("name"))
        }
    }

    private fun logout() {
        // 토큰 비우기 (user_id)
        getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply()
        // 로그아웃 토스트 출력
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
        // 로그인 화면으로 이동하기
        startActivity(Intent(this, LoginActivity::class.java))
        // 스택에 쌓여있는 액티비티들을 모두 종료 (메인, 프로필 편집)
        finishAffinity()
    }
}