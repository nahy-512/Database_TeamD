package gachon.database.instagram.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gachon.database.instagram.databinding.ActivityProfileEditBinding

class ProfileEditActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileEditBinding

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
    }

    private fun setInit() {
        // 값 넣어주기
        with(binding) {
            profileEditUserNameEt.setText(intent.getStringExtra("user_name"))
            profileEditNameEt.setText(intent.getStringExtra("name"))
        }
    }
}