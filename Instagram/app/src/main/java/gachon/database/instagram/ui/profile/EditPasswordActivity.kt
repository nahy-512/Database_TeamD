package gachon.database.instagram.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gachon.database.instagram.R
import gachon.database.instagram.databinding.ActivityEditPasswordBinding

class EditPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPasswordBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initClickListener()
        setInit()
    }

    private fun initClickListener() {
        // 뒤로가기 버튼 클릭
        binding.editPasswordBackIv.setOnClickListener {
            finish()
        }

        // 비밀번호 변경
        binding.editPasswordSaveBtn.setOnClickListener {
            //TODO: DB의 유저 비밀번호 업데이트
        }
    }

    private fun setInit() {
        binding.editPasswordUserNameTv.text = getString(R.string.edit_password_user_name_tv, intent.getStringExtra("user_name"))
    }
}