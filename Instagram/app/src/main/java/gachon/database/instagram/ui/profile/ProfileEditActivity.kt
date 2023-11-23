package gachon.database.instagram.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gachon.database.instagram.databinding.ActivityProfileEditBinding

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
    }

    private fun setInit() {
        userName = intent.getStringExtra("user_name").toString()
        // 값 넣어주기
        with(binding) {
            profileEditUserNameEt.setText(userName)
            profileEditNameEt.setText(intent.getStringExtra("name"))
        }
    }
}